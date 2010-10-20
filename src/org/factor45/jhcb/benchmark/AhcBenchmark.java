package org.factor45.jhcb.benchmark;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
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

    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();

        System.setProperty("com.ning.http.client.logging.LoggerProvider.class",
                           "com.ning.http.client.logging.Slf4jLoggerProvider");

        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaximumConnectionsPerHost(10)
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

                    List<Future<Integer>> futures = new ArrayList<Future<Integer>>(requestsPerThreadPerBatch);
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        try {
                            futures.add(client.prepareGet(url).execute(new AsyncHandler<Integer>() {
                                private HttpResponseStatus httpResponseStatus = null;

                                @Override
                                public void onThrowable(Throwable throwable) {
                                }

                                @Override
                                public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                                    return STATE.CONTINUE;
                                }

                                @Override
                                public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                                    this.httpResponseStatus = status;
                                    return STATE.CONTINUE;
                                }

                                @Override
                                public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
                                    return STATE.CONTINUE;
                                }

                                @Override
                                public Integer onCompleted() throws Exception {
                                    return (httpResponseStatus == null ? 500 : httpResponseStatus.getStatusCode());
                                }
                            }));
                        } catch (IOException e) {
                            System.err.println("Failed to execute request.");
                        }
                    }

                    for (Future<Integer> future : futures) {
                        try {
                            int result = future.get();
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
