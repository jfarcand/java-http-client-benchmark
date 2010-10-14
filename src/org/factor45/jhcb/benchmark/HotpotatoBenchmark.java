package org.factor45.jhcb.benchmark;

import org.factor45.hotpotato.client.HttpClient;
import org.factor45.hotpotato.client.factory.DefaultHttpClientFactory;
import org.factor45.hotpotato.request.HttpRequestFuture;
import org.factor45.hotpotato.util.HostPortAndUri;
import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.ThreadResult;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:bruno.carvalho@wit-software.com">Bruno de Carvalho</a>
 */
public class HotpotatoBenchmark extends AbstractBenchmark {

    // internal vars --------------------------------------------------------------------------------------------------

    protected HttpClient client;
    protected HostPortAndUri target;
    protected HttpRequest request;

    // constructors ---------------------------------------------------------------------------------------------------

    public HotpotatoBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }

    @Override
    protected void setup() {
        super.setup();

        // Mostly defaults
        DefaultHttpClientFactory factory = new DefaultHttpClientFactory();
        factory.setMaxConnectionsPerHost(10);
        this.client = factory.getClient();

        if (!this.client.init()) {
            throw new IllegalStateException("Could not initialise HttpClient");
        }

        this.target = HostPortAndUri.splitUrl(this.url);
        this.request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.target.getUri());
    }

    @Override
    protected void tearDown() {
        super.tearDown();

        this.client.terminate();
    }

    @Override
    protected void warmup() {
        List<HttpRequestFuture<?>> futures = new ArrayList<HttpRequestFuture<?>>(this.warmupRequests);
        for (int i = 0; i < this.warmupRequests; i++) {
            futures.add(this.client.execute(this.target.getHost(), this.target.getPort(), this.request));
        }

        for (int i = 0; i < futures.size(); i++) {
            HttpRequestFuture<?> future = futures.get(i);
            future.awaitUninterruptibly();
            if (!future.isSuccess()) {
                System.err.println("Warmup request #" + i + " failed: " + future.getCause());
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
                    List<HttpRequestFuture<?>> futures = new ArrayList<HttpRequestFuture<?>>(requestsPerThreadPerBatch);

                    int successfulRequests = 0;
                    long start = System.nanoTime();
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        futures.add(client.execute(target.getHost(), target.getPort(), request));
                    }

                    for (HttpRequestFuture<?> future : futures) {
                        future.awaitUninterruptibly();
                        if (!future.isSuccess()) {
                            System.err.println("Request failed: " + future.getCause());
                        } else {
                            successfulRequests++;
                        }
                    }
                    long totalTime = System.nanoTime() - start;
                    threadResults.add(new ThreadResult(requestsPerThreadPerBatch, successfulRequests, totalTime));
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
