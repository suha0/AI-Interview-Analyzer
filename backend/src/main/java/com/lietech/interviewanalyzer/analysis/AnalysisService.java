package com.lietech.interviewanalyzer.analysis;

import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.interview.InterviewRepository;
import com.lietech.interviewanalyzer.security.UserPrincipal;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnalysisService {

    private final AnalysisMetricRepository metrics;
    private final InterviewRepository interviews;

    public AnalysisService(
            AnalysisMetricRepository metrics,
            InterviewRepository interviews
    ) {
        this.metrics = metrics;
        this.interviews = interviews;
    }

    @Transactional
    public MetricResponse record(
            UserPrincipal principal,
            Long interviewId,
            MetricRequest request
    ) {

        Interview interview = ownedInterview(principal, interviewId);

        AnalysisMetric metric = new AnalysisMetric();

        // IMPORTANT
        metric.setInterview(interview);

        metric.setType(request.type());
        metric.setScore(request.score());

        if (request.capturedAt() != null) {
            metric.setCapturedAt(request.capturedAt());
        } else {
            metric.setCapturedAt(Instant.now());
        }

        metric.setMetadata(request.metadata());

        AnalysisMetric saved = metrics.save(metric);

        return MetricResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public InterviewReport report(
            UserPrincipal principal,
            Long interviewId
    ) {

        ownedInterview(principal, interviewId);

        List<MetricResponse> timeline =
                metrics.findByInterviewIdOrderByCapturedAtAsc(interviewId)
                        .stream()
                        .map(MetricResponse::from)
                        .toList();

        Map<MetricType, Double> averages =
                timeline.stream()
                        .collect(Collectors.groupingBy(
                                MetricResponse::type,
                                Collectors.averagingDouble(MetricResponse::score)
                        ));

        double overall =
                averages.values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0);

        DoubleSummaryStatistics confidenceStats =
                timeline.stream()
                        .filter(metric ->
                                metric.type() == MetricType.VOICE_CONFIDENCE)
                        .mapToDouble(MetricResponse::score)
                        .summaryStatistics();

        return new InterviewReport(
                interviewId,
                averages,
                overall,
                confidenceStats.getCount() == 0
                        ? 0
                        : confidenceStats.getAverage(),
                timeline
        );
    }

    private Interview ownedInterview(
            UserPrincipal principal,
            Long interviewId
    ) {

        Interview interview =
                interviews.findById(interviewId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Interview not found"
                                )
                        );

        if (!interview.getCandidateId().equals(principal.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return interview;
    }
}