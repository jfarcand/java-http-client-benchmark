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

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.ThreadResult;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class ApacheBenchmark extends AbstractBenchmark {

    // internal vars --------------------------------------------------------------------------------------------------

    private HttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public ApacheBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }

    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();

        HttpParams params = new BasicHttpParams();
        params.setParameter(HttpProtocolParams.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        params.setBooleanParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                false);
        params.setBooleanParameter(HttpConnectionParams.STALE_CONNECTION_CHECK,
                false);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE,
                8 * 1024);
        ConnManagerParams.setMaxTotalConnections(params, 10);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        this.client = new DefaultHttpClient(cm, params);
    }

    @Override
    protected void tearDown() {
        super.tearDown();

        this.client.getConnectionManager().shutdown();
    }

    @Override
    protected void warmup() {
        for (int i = 0; i < this.warmupRequests; i++) {
            HttpGet get = new HttpGet(this.url);
            try {
                HttpResponse response = this.client.execute(get);
                response.getEntity().consumeContent();
            } catch (IOException e) {
                get.abort();
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
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        HttpGet get = new HttpGet(url);
                        try {
                            HttpResponse response = client.execute(get);
                            response.getEntity().consumeContent();
                            if (response.getStatusLine().getStatusCode() == 200) {
                                successful++;
                            }
                        } catch (IOException e) {
                            get.abort();
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
