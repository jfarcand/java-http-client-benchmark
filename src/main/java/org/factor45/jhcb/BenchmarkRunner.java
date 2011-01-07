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
import org.factor45.jhcb.result.BenchmarkResult;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class BenchmarkRunner {

    private final static String TARGET_URL = "http://10.0.1.3:8081";

    // public static methods ------------------------------------------------------------------------------------------

    public static void runHotpotatoBenchmark() {
        AbstractBenchmark benchmark = new HotpotatoBenchmark(100, 100, 100, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runHotpotatoPipeliningBenchmark() {
        AbstractBenchmark benchmark = new HotpotatoPipeliningBenchmark(10, 10, 50, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runApacheBenchmark() {
        AbstractBenchmark benchmark = new ApacheBenchmark(100, 100, 100, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    public static void runAhcBenchmark() {
        AbstractBenchmark benchmark = new AhcBenchmark(100, 100, 100, TARGET_URL);
        BenchmarkResult result = benchmark.doBenchmark();
        System.out.println(result);
    }

    // main -----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws InterruptedException {
        runAhcBenchmark();
        runHotpotatoBenchmark();
        runApacheBenchmark();
    }
}
