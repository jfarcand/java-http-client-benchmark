package org.factor45.jhcb;

import org.factor45.jhcb.benchmark.AbstractBenchmark;
import org.factor45.jhcb.benchmark.AhcBenchmark;
import org.factor45.jhcb.benchmark.ApacheBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoPipeliningBenchmark;
import org.factor45.jhcb.result.BenchmarkResult;

/**
 * @author <a href="mailto:bruno.carvalho@wit-software.com">Bruno de Carvalho</a>
 */
public class BenchmarkRunner {

    // public static methods ------------------------------------------------------------------------------------------

    public static void runHotpotatoBenchmark() {
        //HotpotatoBenchmark benchmark = new HotpotatoBenchmark(1, 1000, 100, "http://localhost:8080/uri");
        //HotpotatoBenchmark benchmark = new HotpotatoBenchmark(10, 100, 100, "http://localhost:8080/uri");
        AbstractBenchmark benchmark = new HotpotatoBenchmark(100, 10, 100, "http://localhost:8080/uri");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runHotpotatoPipeliningBenchmark() {
        //HotpotatoBenchmark benchmark = new HotpotatoPipeliningBenchmark(1, 1000, 100, "http://localhost:8080/uri");
        //HotpotatoBenchmark benchmark = new HotpotatoPipeliningBenchmark(10, 100, 100, "http://localhost:8080/uri");
        AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(100, 10, 100, "http://localhost:8080/uri");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runApacheBenchmark() {
        AbstractBenchmark benchmark = new ApacheBenchmark(10, 100, 100, "http://localhost:8080/uri");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runAhcBenchmark() {
        AbstractBenchmark benchmark = new AhcBenchmark(1, 1000, 100, "http://localhost:8080/uri");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    // main -----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        //runHotpotatoBenchmark();
        runHotpotatoPipeliningBenchmark();
        //runApacheBenchmark();
        //runAhcBenchmark();
    }
}
