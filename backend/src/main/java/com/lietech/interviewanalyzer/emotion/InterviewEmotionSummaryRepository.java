package com.lietech.interviewanalyzer.emotion;

import com.lietech.interviewanalyzer.interview.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewEmotionSummaryRepository
        extends JpaRepository<InterviewEmotionSummary, Long> {

    Optional<InterviewEmotionSummary> findByInterview(Interview interview);
}