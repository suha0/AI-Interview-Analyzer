package com.lietech.interviewanalyzer.emotion;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/emotion-summary")
public class EmotionSummaryController {

    private final EmotionSummaryService emotionSummaryService;

    public EmotionSummaryController(EmotionSummaryService emotionSummaryService) {
        this.emotionSummaryService = emotionSummaryService;
    }

    @GetMapping("/{interviewId}")
    public Map<String, Object> getSummary(@PathVariable Long interviewId) {
        return emotionSummaryService.getSummary(interviewId);
    }
}