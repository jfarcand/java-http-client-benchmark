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
package org.factor45.jhcb;

import org.factor45.jhcb.benchmark.AbstractBenchmark;
import org.factor45.jhcb.benchmark.AhcBenchmark;
import org.factor45.jhcb.benchmark.ApacheBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoBenchmark;
import org.factor45.jhcb.benchmark.HotpotatoPipeliningBenchmark;
import org.factor45.jhcb.benchmark.SimpleAhcBenchmark;
import org.factor45.jhcb.result.BenchmarkResult;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class BenchmarkRunner {

    private static String TARGET_URL = "http://10.0.1.4:8081";

    // public static methods ------------------------------------------------------------------------------------------

    public static void runHotpotatoBenchmark() {
        AbstractBenchmark benchmark = new HotpotatoBenchmark(50, 50, 50, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runHotpotatoPipeliningBenchmark() {
        AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(10, 10, 50, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runApacheBenchmark() {
        AbstractBenchmark benchmark = new ApacheBenchmark(50, 50, 50, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }

    public static void runAhcBenchmark() {
        AbstractBenchmark benchmark = new AhcBenchmark(50, 50, 50, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }

    public static void runSimpleAhcBenchmark()
    {
        AbstractBenchmark benchmark = new SimpleAhcBenchmark( 50, 50, 50, TARGET_URL );
        BenchmarkResult result = benchmark.doBenchmark();
        System.err.println( result );
    }

    // main -----------------------------------------------------------------------------------------------------------

    public static void main( String[] args )
        throws Exception
    {
        if (args.length > 0 && args[0] != null)
            TARGET_URL = args[0];

        runSimpleAhcBenchmark();
        runApacheBenchmark();
        runAhcBenchmark();

        // Hot Potato perform pretty well but doesn't support all the features needed to be considered stable.
        // runHotpotatoBenchmark();
        // runApacheBenchmark();
    }
}
