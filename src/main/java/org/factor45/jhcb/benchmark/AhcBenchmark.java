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

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProviderConfig;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
import com.ning.http.client.logging.LogManager;
import com.ning.http.client.logging.LoggerProvider;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.ThreadResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class AhcBenchmark extends AbstractBenchmark {

    // internal vars --------------------------------------------------------------------------------------------------

    private AsyncHttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public AhcBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }

    public static void setUpLogger() {
        final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("UnitTest");
        LogManager.setProvider(new LoggerProvider() {

            public com.ning.http.client.logging.Logger getLogger(final Class<?> clazz) {
                return new com.ning.http.client.logging.Logger() {

                    public boolean isDebugEnabled() {
                        return true;
                    }

                    public void debug(final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                    }

                    public void debug(final Throwable t) {
                        t.printStackTrace();
                    }

                    public void debug(final Throwable t, final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                        t.printStackTrace();
                    }

                    public void info(final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                    }

                    public void info(final Throwable t) {
                        t.printStackTrace();
                    }

                    public void info(final Throwable t, final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                        t.printStackTrace();
                    }

                    public void warn(final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                    }

                    public void warn(final Throwable t) {
                        t.printStackTrace();
                    }

                    public void warn(final Throwable t, final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                        t.printStackTrace();
                    }

                    public void error(final String msg, final Object... msgArgs) {
                        System.out.println(msg);

                    }

                    public void error(final Throwable t) {
                        t.printStackTrace();
                    }

                    public void error(final Throwable t, final String msg, final Object... msgArgs) {
                        System.out.println(msg);
                        t.printStackTrace();
                    }
                };
            }
        });
    }


    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();
        //setUpLogger();
        System.setProperty("com.ning.http.client.logging.LoggerProvider.class",
                "com.ning.http.client.logging.Slf4jLoggerProvider");

        AsyncHttpProviderConfig c = new NettyAsyncHttpProviderConfig();
        c.addProperty(NettyAsyncHttpProviderConfig.USE_BLOCKING_IO, true);

        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaximumConnectionsPerHost(10)
                .setConnectionTimeoutInMs(0)
                .build();
        this.client = new AsyncHttpClient(config);

    }

    @Override
    protected void tearDown() {
        super.tearDown();

        this.client.close();
    }

    @Override
    protected void warmup() {
        List<Future<Response>> futures = new ArrayList<Future<Response>>(this.warmupRequests);
        for (int i = 0; i < this.warmupRequests; i++) {
            try {
                futures.add(this.client.prepareGet(this.url).execute());
            } catch (IOException e) {
                System.err.println("Failed to execute get at iteration #" + i);
            }
        }

        for (Future<Response> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected BatchResult runBatch() {
        final CountDownLatch latch = new CountDownLatch(this.threads);
        final Vector<ThreadResult> threadResults = new Vector<ThreadResult>(this.threads);

        long batchStart = System.nanoTime();
        for (int i = 0; i < this.threads; i++) {
            this.executor.submit(new Runnable() {

                @Override
                public void run() {
                    int successful = 0;
                    long start = System.nanoTime();

                    List<Future<Response>> futures = new ArrayList<Future<Response>>(requestsPerThreadPerBatch);
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        try {
                            futures.add(client.prepareGet(url).execute());
                        } catch (IOException e) {
                            System.err.println("Failed to execute request.");
                        }
                    }

                    for (Future<Response> future : futures) {
                        try {
                            int result = future.get().getStatusCode();
                            if ((result >= 200) && (result <= 299)) {
                                successful++;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    long totalTime = System.nanoTime() - start;
                    threadResults.add(new ThreadResult(requestsPerThreadPerBatch, successful, totalTime));
                    latch.countDown();
                }
            });
        }


        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        long batchTotalTime = System.nanoTime() - batchStart;

        return new BatchResult(threadResults, batchTotalTime);
    }
}
