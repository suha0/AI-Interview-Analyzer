package com.lietech.interviewanalyzer.emotion;

public class EmotionResult {

    private EmotionType emotion;
    private double confidenceScore;
    private String explanation;

    public EmotionType getEmotion() {
        return emotion;
    }

    public void setEmotion(EmotionType emotion) {
        this.emotion = emotion;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}