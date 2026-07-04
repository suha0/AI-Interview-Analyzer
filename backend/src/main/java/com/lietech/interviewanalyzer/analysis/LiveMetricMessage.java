package com.lietech.interviewanalyzer.analysis;

import java.time.Instant;
import java.util.Map;

public record LiveMetricMessage(
        Long interviewId,
        MetricType type,
        double score,
        Instant capturedAt,
        Map<String, Object> details
) {
}