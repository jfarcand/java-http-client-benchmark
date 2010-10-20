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

    public static void runHotpotatoBenchmark() {
        //AbstractBenchmark benchmark = new HotpotatoBenchmark(1, 1000, 100, "http://10.0.0.2:8090/index.html");
        //AbstractBenchmark benchmark = new HotpotatoBenchmark(10, 100, 100, "http://10.0.0.2:8080/index.html");
        AbstractBenchmark benchmark = new HotpotatoBenchmark(100, 10, 100, "http://10.0.0.2:8080/index.html");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runHotpotatoPipeliningBenchmark() {
        //AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(1, 1000, 100, "http://10.0.0.2:8080/index.html");
        //AbstractoBenchmark benchmark = new HotpotatoPipeliningBenchmark(10, 100, 100, "http://10.0.0.2:8080/index.html");
        AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(100, 10, 100, "http://10.0.0.2:8080/index.html");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runApacheBenchmark() {
        //AbstractBenchmark benchmark = new ApacheBenchmark(1, 1000, 100, "http://10.0.0.2:8080/index.html");
        //AbstractBenchmark benchmark = new ApacheBenchmark(10, 100, 100, "http://10.0.0.2:8080/index.html");
        AbstractBenchmark benchmark = new ApacheBenchmark(100, 10, 100, "http://10.0.0.2:8080/index.html");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runAhcBenchmark() {
        //AbstractBenchmark benchmark = new AhcBenchmark(1, 1000, 100, "http://10.0.0.2:8080/index.html");
        //AbstractBenchmark benchmark = new AhcBenchmark(10, 100, 100, "http://10.0.0.2:8080/index.html");
        AbstractBenchmark benchmark = new AhcBenchmark(100, 10, 100, "http://10.0.0.2:8080/index.html");
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    // main -----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        //runHotpotatoBenchmark();
        //runHotpotatoPipeliningBenchmark();
        //runApacheBenchmark();
        runAhcBenchmark();
    }
}
