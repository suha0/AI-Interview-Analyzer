package com.lietech.interviewanalyzer.analysis;

import com.lietech.interviewanalyzer.interview.Interview;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "analysis_metrics")
public class AnalysisMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Enumerated(EnumType.STRING)
    private MetricType type;

    private double score;

    private Instant capturedAt = Instant.now();

    @Column(columnDefinition = "TEXT")
    private String metadata;

    // ===== GETTERS AND SETTERS =====

    public Long getId() {
        return id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}