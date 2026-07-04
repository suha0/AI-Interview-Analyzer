package com.lietech.interviewanalyzer.analysis;

import java.time.Instant;

public record MetricResponse(
        Long id,
        MetricType type,
        double score,
        Instant capturedAt,
        String metadata
) {

    public static MetricResponse from(AnalysisMetric metric) {

        return new MetricResponse(
                metric.getId(),
                metric.getType(),
                metric.getScore(),
                metric.getCapturedAt(),
                metric.getMetadata()
        );
    }
}