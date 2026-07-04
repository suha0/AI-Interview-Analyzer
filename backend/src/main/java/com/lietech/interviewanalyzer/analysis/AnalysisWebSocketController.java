package com.lietech.interviewanalyzer.analysis;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class AnalysisWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public AnalysisWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/interviews/{interviewId}/metrics")
    public void publishLiveMetric(
            @DestinationVariable Long interviewId,
            LiveMetricMessage message
    ) {
        messagingTemplate.convertAndSend(
                "/topic/interviews/" + interviewId + "/analytics",
                message
        );
    }
}