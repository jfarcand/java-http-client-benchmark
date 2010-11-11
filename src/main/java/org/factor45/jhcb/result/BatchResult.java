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
package org.factor45.jhcb.result;

import java.util.Collection;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class BatchResult {

    // internal vars --------------------------------------------------------------------------------------------------

    private final int batchTargetRequests;
    private final int batchSuccessfulRequests;
    private final long totalTimeForAllThreads;
    private final float averageTimePerThread;
    private final float averageTimePerRequest;
    private final long totalBatchTime;

    // constructors ---------------------------------------------------------------------------------------------------

    public BatchResult(Collection<ThreadResult> threadResults, long totalTime) {
        int targetRequests = 0;
        int successfulRequests = 0;
        long totalTimeForAllThreads = 0;

        for (ThreadResult threadResult : threadResults) {
            targetRequests += threadResult.getTargetRequests();
            successfulRequests += threadResult.getSuccessfulRequests();
            totalTimeForAllThreads += threadResult.getTotalTime();
        }

        this.batchTargetRequests = targetRequests;
        this.batchSuccessfulRequests = successfulRequests;
        this.totalTimeForAllThreads = totalTimeForAllThreads;

        this.averageTimePerThread = totalTimeForAllThreads / (float) threadResults.size();
        // averageTimePerThread because of the overhead of launching threads - averageTimePerThread
        // is the average time that each thread spent *after* being launched while totalTime is the time it took to
        // launch all threads and wait for them to stop.
        // This approach is utopical since it assumes that all threads will be launched and running at the same time.
        //this.averageTimePerRequest = this.averageTimePerThread / (float) successfulRequests;

        // This variation assumes that thread launching overhead is not significant. It's more realisting even though
        // it may be affected by thread launching overhead.
        this.averageTimePerRequest = totalTime / (float) successfulRequests;
        this.totalBatchTime = totalTime;
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public int getBatchTargetRequests() {
        return batchTargetRequests;
    }

    public int getBatchSuccessfulRequests() {
        return batchSuccessfulRequests;
    }

    public long getTotalTimeForAllThreads() {
        return totalTimeForAllThreads;
    }

    public float getAverageTimePerThread() {
        return averageTimePerThread;
    }

    public float getAverageTimePerRequest() {
        return averageTimePerRequest;
    }

    public long getTotalBatchTime() {
        return totalBatchTime;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "BatchResult{" +
               "requestsPerSecond=" + BenchmarkResult.decimal(1000000000f / this.averageTimePerRequest) +
               ", batchTargetRequests=" + batchTargetRequests +
               ", batchSuccessfulRequests=" + batchSuccessfulRequests +
               ", totalTimeForAllThreads=" + BenchmarkResult.decimal(totalTimeForAllThreads / 1000000f) +
               "ms, averageTimePerThread=" + BenchmarkResult.decimal(averageTimePerThread / 1000000f) +
               "ms, averageTimePerRequest=" + BenchmarkResult.decimal(averageTimePerRequest / 1000000f) +
               "ms, totalBatchTime=" + BenchmarkResult.decimal(totalBatchTime / 1000000f) +
               "ms}";
    }
}
