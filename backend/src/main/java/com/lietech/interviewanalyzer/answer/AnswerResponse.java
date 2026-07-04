package com.lietech.interviewanalyzer.answer;

public class AnswerResponse {

    private Long id;
    private Integer score;
    private String feedback;

    private String emotion;
    private Double emotionScore;
    private String emotionExplanation;

    // NEW FIELD
    private String followUpQuestion;

    public AnswerResponse(
            Long id,
            Integer score,
            String feedback,
            String emotion,
            Double emotionScore,
            String emotionExplanation,
            String followUpQuestion
    ) {
        this.id = id;
        this.score = score;
        this.feedback = feedback;
        this.emotion = emotion;
        this.emotionScore = emotionScore;
        this.emotionExplanation = emotionExplanation;
        this.followUpQuestion = followUpQuestion;
    }

    public Long getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getEmotion() {
        return emotion;
    }

    public Double getEmotionScore() {
        return emotionScore;
    }

    public String getEmotionExplanation() {
        return emotionExplanation;
    }

    // NEW GETTER
    public String getFollowUpQuestion() {
        return followUpQuestion;
    }
}