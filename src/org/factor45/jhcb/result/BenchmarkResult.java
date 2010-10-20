package org.factor45.jhcb.result;

import java.util.List;

/**
 * @author <a href="http://bruno.factor45.org/">Bruno de Carvalho</a>
 */
public class BenchmarkResult {

    // internal vars --------------------------------------------------------------------------------------------------

    private final int threads;
    private final int batches;
    private final long targetRequests;
    private final long successfulRequests;
    private final long failedRequests;
    private final float averageRequestTime;
    private final float averageBatchTime;
    private final long totalBenchmarkTime;
    private final float requestsPerSecond;
    private final float minRequestsPerSecond;
    private final float maxRequestsPerSecond;

    // constructors ---------------------------------------------------------------------------------------------------

    public BenchmarkResult(int threads, int batches, List<BatchResult> results) {
        this.threads = threads;
        this.batches = batches;

        long totalTargetRequests = 0;
        long totalSuccessfulRequests = 0;
        long totalFailedRequests = 0;
        long totalBenchmarkTime = 0;
        float totalRequestAverage = 0;

        float minRequestAverage = Float.MAX_VALUE;
        float maxRequestAverage = 0;

        for (BatchResult result : results) {
            totalTargetRequests += result.getBatchTargetRequests();
            totalSuccessfulRequests += result.getBatchSuccessfulRequests();
            totalFailedRequests += result.getBatchTargetRequests() - result.getBatchSuccessfulRequests();
            totalBenchmarkTime += result.getTotalBatchTime();
            totalRequestAverage += result.getAverageTimePerRequest();

            if (result.getAverageTimePerRequest() < minRequestAverage) {
                minRequestAverage = result.getAverageTimePerRequest();
            }

            if (result.getAverageTimePerRequest() > maxRequestAverage) {
                maxRequestAverage = result.getAverageTimePerRequest();
            }
        }

        this.targetRequests = totalTargetRequests;
        this.successfulRequests = totalSuccessfulRequests;
        this.failedRequests = totalFailedRequests;

        this.averageRequestTime = totalRequestAverage / (float) results.size();
        this.averageBatchTime = totalBenchmarkTime / (float) results.size();
        this.totalBenchmarkTime = totalBenchmarkTime;
        this.requestsPerSecond = 1000000000f / this.averageRequestTime;
        this.maxRequestsPerSecond = 1000000000f / minRequestAverage;
        this.minRequestsPerSecond = 1000000000f / maxRequestAverage;
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static String decimal(double d) {
        return decimal(d, 2);
    }

    public static String decimal(double d, int decimalUnits) {
        if (decimalUnits <= 0) {
            return Double.toString(d);
        }

        String s = Double.toString(d);
        int pos = s.indexOf('.');
        return s.substring(0, Math.min(pos + 1 + decimalUnits, s.length())); // xxxx.yy
    }

    public static String decimal(float f) {
        return decimal(f, 2);
    }

    public static String decimal(float f, int decimalUnits) {
        if (decimalUnits <= 0) {
            return Float.toString(f);
        }

        String s = Float.toString(f);
        int pos = s.indexOf('.');
        return s.substring(0, Math.min(pos + 1 + decimalUnits, s.length())); // xxxx.yy
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public int getThreads() {
        return threads;
    }

    public int getBatches() {
        return batches;
    }

    public long getTargetRequests() {
        return targetRequests;
    }

    public long getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getFailedRequests() {
        return failedRequests;
    }

    public float getAverageRequestTime() {
        return averageRequestTime;
    }

    public float getAverageBatchTime() {
        return averageBatchTime;
    }

    public long getTotalBenchmarkTime() {
        return totalBenchmarkTime;
    }

    public float getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public float getMinRequestsPerSecond() {
        return minRequestsPerSecond;
    }

    public float getMaxRequestsPerSecond() {
        return maxRequestsPerSecond;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "BenchmarkResult{" +
               "requestsPerSecond=" + decimal(requestsPerSecond) +
               "(max: " + decimal(maxRequestsPerSecond) + ", min: " + decimal(minRequestsPerSecond) + 
               "), threads=" + threads +
               ", batches=" + batches +
               ", targetRequests=" + targetRequests +
               ", successfulRequests=" + successfulRequests +
               ", failedRequests=" + failedRequests +
               ", averageRequestTime=" + decimal(averageRequestTime * 1000000f) +
               "ms, averageBatchTime=" + decimal(averageBatchTime * 1000000f) +
               "ms, totalBenchmarkTime=" + decimal(totalBenchmarkTime * 1000000f) +
               "ms}";
    }
}
