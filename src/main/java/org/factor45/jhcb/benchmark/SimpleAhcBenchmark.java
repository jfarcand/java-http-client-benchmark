/*
 * Copyright 2010 Bruno de Carvalho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.factor45.jhcb.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.ThreadResult;

import com.ning.http.client.Response;
import com.ning.http.client.SimpleAsyncHttpClient;
import com.ning.http.client.ThrowableHandler;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class SimpleAhcBenchmark
    extends AbstractBenchmark
{

    // internal vars --------------------------------------------------------------------------------------------------

    private SimpleAsyncHttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public SimpleAhcBenchmark( int threads, int requestsPerThreadPerBatch, int batches, String uri )
    {
        super( threads, requestsPerThreadPerBatch, batches, uri );
    }

    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup()
    {
        super.setup();

        this.client =
            new SimpleAsyncHttpClient.Builder().setMaximumConnectionsPerHost( 10 ).setConnectionTimeoutInMs( 0 ).build();
    }

    @Override
    protected void tearDown()
    {
        super.tearDown();

        this.client.close();
    }

    @Override
    protected void warmup()
    {
        List<Future<Response>> futures = new ArrayList<Future<Response>>( this.warmupRequests );
        for ( int i = 0; i < this.warmupRequests; i++ )
        {
            try
            {
                futures.add( this.client.derive().setUrl( this.url ).build().get() );
            }
            catch ( IOException e )
            {
                System.err.println( "Failed to execute get at iteration #" + i );
            }
        }

        for ( Future<Response> future : futures )
        {
            try
            {
                future.get();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
            catch ( ExecutionException e )
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected BatchResult runBatch()
    {
        final CountDownLatch latch = new CountDownLatch( this.threads );
        final Vector<ThreadResult> threadResults = new Vector<ThreadResult>( this.threads );

        long batchStart = System.nanoTime();
        for ( int i = 0; i < this.threads; i++ )
        {
            this.executor.submit( new Runnable()
            {

                @Override
                public void run()
                {
                    final AtomicInteger successful = new AtomicInteger();
                    long start = System.nanoTime();
                    final CountDownLatch responseReceivedLatch = new CountDownLatch( requestsPerThreadPerBatch );
                    for ( int i = 0; i < requestsPerThreadPerBatch; i++ )
                    {
                        try
                        {
                            SimpleAsyncHttpClient derived = client.derive().setUrl( url ).build();

                            Future<Response> future = derived.get( new ThrowableHandler()
                            {
                                @Override
                                public void onThrowable( Throwable t )
                                {
                                    responseReceivedLatch.countDown();
                                }

                            } );

                            try
                            {
                                Response response = future.get();

                                if ( ( response.getStatusCode() >= 200 ) && ( response.getStatusCode() <= 299 ) )
                                {
                                    successful.incrementAndGet();
                                }
                            }
                            catch ( InterruptedException e )
                            {
                                System.err.println( "Failed to execute request." + e.getMessage() );
                            }
                            catch ( ExecutionException e )
                            {
                                System.err.println( "Failed to execute request." + e.getMessage() );
                            }
                            finally
                            {
                                responseReceivedLatch.countDown();
                                derived.close();
                            }

                        }
                        catch ( IOException e )
                        {
                            System.err.println( "Failed to execute request." );
                        }
                    }

                    try
                    {
                        responseReceivedLatch.await();
                    }
                    catch ( InterruptedException e )
                    {
                        e.printStackTrace();
                    }
                    long totalTime = System.nanoTime() - start;
                    threadResults.add( new ThreadResult( requestsPerThreadPerBatch, successful.get(), totalTime ) );
                    latch.countDown();
                }
            }

            );
        }

        try
        {
            latch.await();
        }
        catch ( InterruptedException e )
        {
            Thread.interrupted();
        }
        long batchTotalTime = System.nanoTime() - batchStart;

        return new BatchResult( threadResults, batchTotalTime );
    }
}
