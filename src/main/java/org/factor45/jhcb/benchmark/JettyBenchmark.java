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

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.ThreadResult;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class JettyBenchmark extends AbstractBenchmark {
    // internal vars --------------------------------------------------------------------------------------------------

    private HttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public JettyBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }
    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();
        this.client = new HttpClient();
        this.client.setRequestBufferSize(8 * 1024);
        this.client.setResponseBufferSize(8 * 1024);
        this.client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
        this.client.setMaxConnectionsPerAddress(10);
    }

    @Override
    protected void tearDown() {
        super.tearDown();

        try {
            this.client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void warmup() {
        try {
            this.client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < this.warmupRequests; i++) {

            ContentExchange exchange = new ContentExchange();

            exchange.setURL(this.url);

            try {
                this.client.send(exchange);
                try {
                    exchange.waitForDone();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
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
                    final AtomicInteger successful = new AtomicInteger();
                    long start = System.nanoTime();
                    final CountDownLatch responseReceivedLatch = new CountDownLatch(requestsPerThreadPerBatch);
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        ContentExchange exchange = new ContentExchange();

                        exchange.setURL(url);

                        try {
                            client.send(exchange);
                            try {
                                exchange.waitForDone();
                                if (exchange.getResponseStatus() == 200) {
                                    successful.incrementAndGet();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                    long totalTime = System.nanoTime() - start;
                    threadResults.add(new ThreadResult(requestsPerThreadPerBatch, successful.get(), totalTime));
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