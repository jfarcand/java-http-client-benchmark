package org.factor45.jhcb.benchmark;

import org.factor45.jhcb.result.BatchResult;
import org.factor45.jhcb.result.BenchmarkResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public abstract class AbstractBenchmark {


    // constants ------------------------------------------------------------------------------------------------------

    protected static final int WARMUP_REQUESTS = 1000;

    // configuration --------------------------------------------------------------------------------------------------

    protected final int threads;
    protected final int requestsPerThreadPerBatch;
    protected final int batches;
    protected final String url;
    protected int warmupRequests;

    // internal vars --------------------------------------------------------------------------------------------------

    protected ExecutorService executor;

    // constructors ---------------------------------------------------------------------------------------------------

    public AbstractBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        if (threads < 1) {
            throw new IllegalArgumentException("Thread count must be > 1");
        }

        this.threads = threads;
        this.requestsPerThreadPerBatch = requestsPerThreadPerBatch;
        this.batches = batches;
        this.url = url;

        this.warmupRequests = WARMUP_REQUESTS;
    }

    public BenchmarkResult doBenchmark() {
        System.err.println("Setting up " + this.getClass().getSimpleName() + "...");
        this.setup();
        System.err.println("Beginning warmup stage...");
        this.warmup();
        System.err.println("Warmup complete, running " + this.batches + " batches...");

        List<BatchResult> results = new ArrayList<BatchResult>(this.batches);
        for (int i = 0; i < this.batches; i++) {
            BatchResult result = this.runBatch();
            results.add(result);
            System.err.println("Batch " + i + " finished: " + result);
        }

        System.err.println("Test finished, shutting down and calculating results...");
        this.tearDown();
        return new BenchmarkResult(this.threads, this.batches, results);
    }

    protected void setup() {
        this.executor = Executors.newFixedThreadPool(this.threads);
    }

    protected void tearDown() {
        this.executor.shutdown();
    }

    protected abstract void warmup();

    protected abstract BatchResult runBatch();

    // getters & setters ----------------------------------------------------------------------------------------------

    public int getThreads() {
        return threads;
    }

    public int getRequestsPerThreadPerBatch() {
        return requestsPerThreadPerBatch;
    }

    public int getBatches() {
        return batches;
    }

    public int getWarmupRequests() {
        return warmupRequests;
    }

    public void setWarmupRequests(int warmupRequests) {
        if (warmupRequests < 1) {
            throw new IllegalArgumentException("Minimum warmup requests is 1");
        }
        this.warmupRequests = warmupRequests;
    }
}
