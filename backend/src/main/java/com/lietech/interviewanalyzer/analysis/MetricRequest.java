package com.lietech.interviewanalyzer.analysis;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record MetricRequest(
        @NotNull MetricType type,
        @Min(0) @Max(100) double score,
        Instant capturedAt,
        String metadata
) {
}
