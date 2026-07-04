package com.lietech.interviewanalyzer.analysis;

import java.util.List;
import java.util.Map;

public record InterviewReport(
        Long interviewId,
        Map<MetricType, Double> averageScores,
        double overallScore,
        double voiceConfidenceAverage,
        List<MetricResponse> timeline
) {
}