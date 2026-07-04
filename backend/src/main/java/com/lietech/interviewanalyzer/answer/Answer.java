package com.lietech.interviewanalyzer.answer;

import com.lietech.interviewanalyzer.interview.Interview;
import com.lietech.interviewanalyzer.question.Question;
import jakarta.persistence.*;

@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "score")
    private Integer score;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;


    // ============================
    // 🧠 EMOTION FIELDS (STRUCTURED)
    // ============================

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "emotion_score")
    private Double emotionScore;

    @Column(name = "emotion_explanation", columnDefinition = "TEXT")
    private String emotionExplanation;

    // ============================
    // 🧠 EMOTION LOB (FULL RAW DATA)
    // ============================
    @Column(columnDefinition = "TEXT")
    private String emotionBlob;

    // ============================
    // GETTERS & SETTERS
    // ============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Double getEmotionScore() {
        return emotionScore;
    }

    public void setEmotionScore(Double emotionScore) {
        this.emotionScore = emotionScore;
    }

    public String getEmotionExplanation() {
        return emotionExplanation;
    }

    public void setEmotionExplanation(String emotionExplanation) {
        this.emotionExplanation = emotionExplanation;
    }

    public String getEmotionBlob() {
        return emotionBlob;
    }

    public void setEmotionBlob(String emotionBlob) {
        this.emotionBlob = emotionBlob;
    }
}