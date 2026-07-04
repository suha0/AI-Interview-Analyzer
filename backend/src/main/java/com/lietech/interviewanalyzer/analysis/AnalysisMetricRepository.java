package com.lietech.interviewanalyzer.analysis;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisMetricRepository
        extends JpaRepository<AnalysisMetric, Long> {

    List<AnalysisMetric> findByInterviewIdOrderByCapturedAtAsc(
            Long interviewId
    );
}