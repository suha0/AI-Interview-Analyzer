package com.lietech.interviewanalyzer.emotion;

import com.lietech.interviewanalyzer.interview.Interview;
import jakarta.persistence.*;

@Entity
@Table(name = "interview_emotion_summary")
public class InterviewEmotionSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    private Integer totalAnswers;
    private Double avgEmotionScore;

    private Integer confidentCount;
    private Integer nervousCount;
    private Integer negativeCount;
    private Integer neutralCount;

    private String finalEmotion; // CONFIDENT / NERVOUS / MIXED

    // getters & setters

    public Long getId() { return id; }

    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }

    public Integer getTotalAnswers() { return totalAnswers; }
    public void setTotalAnswers(Integer totalAnswers) { this.totalAnswers = totalAnswers; }

    public Double getAvgEmotionScore() { return avgEmotionScore; }
    public void setAvgEmotionScore(Double avgEmotionScore) { this.avgEmotionScore = avgEmotionScore; }

    public Integer getConfidentCount() { return confidentCount; }
    public void setConfidentCount(Integer confidentCount) { this.confidentCount = confidentCount; }

    public Integer getNervousCount() { return nervousCount; }
    public void setNervousCount(Integer nervousCount) { this.nervousCount = nervousCount; }

    public Integer getNegativeCount() { return negativeCount; }
    public void setNegativeCount(Integer negativeCount) { this.negativeCount = negativeCount; }

    public Integer getNeutralCount() { return neutralCount; }
    public void setNeutralCount(Integer neutralCount) { this.neutralCount = neutralCount; }

    public String getFinalEmotion() { return finalEmotion; }
    public void setFinalEmotion(String finalEmotion) { this.finalEmotion = finalEmotion; }
}