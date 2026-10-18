package com.cricketscore.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class WebSocketEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventPublisher.class);

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishScoreUpdate(ScoreUpdateMessage message) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendToSubscribers(message);
                }
            });
        } else {
            sendToSubscribers(message);
        }
    }

    private void sendToSubscribers(ScoreUpdateMessage message) {
        try {
            String matchTopic = "/topic/matches/" + message.getMatchId() + "/score";
            messagingTemplate.convertAndSend(matchTopic, message);
            log.info("Published WebSocket event {} to destination {}", message.getEventType(), matchTopic);
        } catch (Exception ex) {
            log.error("Failed to broadcast WebSocket score update: {}", ex.getMessage());
        }
    }
}
