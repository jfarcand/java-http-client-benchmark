package org.factor45.jhcb;

import org.factor45.jhcb.benchmark.AbstractBenchmark;
import org.factor45.jhcb.benchmark.AhcBenchmark;
import org.factor45.jhcb.benchmark.ApacheBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoPipeliningBenchmark;
import org.factor45.jhcb.result.BenchmarkResult;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class BenchmarkRunner {

    // public static methods ------------------------------------------------------------------------------------------

    public static void runHotpotatoBenchmark(int threads, int requestsPerThread, int batches, String url) {
        AbstractBenchmark benchmark = new HotpotatoBenchmark(threads, requestsPerThread, batches, url);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runHotpotatoPipeliningBenchmark(int threads, int requestsPerThread, int batches, String url) {
        AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(threads, requestsPerThread, batches, url);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runApacheBenchmark(int threads, int requestsPerThread, int batches, String url) {
        AbstractBenchmark benchmark = new ApacheBenchmark(threads, requestsPerThread, batches, url);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runAhcBenchmark(int threads, int requestsPerThread, int batches, String url) {
        AbstractBenchmark benchmark = new AhcBenchmark(threads, requestsPerThread, batches, url);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    // main -----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        String url = "http://10.0.0.2:8080/index.html";

        // 1 thread, 1000 requests per thread, 100 batches
        runHotpotatoBenchmark(1, 1000, 100, url);
//        runHotpotatoPipeliningBenchmark(1, 1000, 100, url);
//        runApacheBenchmark(1, 1000, 100, url);
//        runAhcBenchmark(1, 1000, 100, url);

        // 10 threads, 100 requests per thread, 100 batches
//        runHotpotatoBenchmark(10, 100, 100, url);
//        runHotpotatoPipeliningBenchmark(10, 100, 100, url);
//        runApacheBenchmark(10, 100, 100, url);
//        runAhcBenchmark(10, 100, 100, url);

        // 100 threads, 10 requests per thread, 100 batches
//        runHotpotatoBenchmark(100, 10, 100, url);
//        runHotpotatoPipeliningBenchmark(100, 10, 100, url);
//        runApacheBenchmark(100, 10, 100, url);
//        runAhcBenchmark(100, 10, 100, url);
    }
}
