package com.pm.notificationservice.service;

import com.pm.notificationservice.dto.response.NotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

    // 1 Hour timeout
    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE Connection completed for userId: {}", userId);
            emitters.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE Connection timed out for userId: {}", userId);
            emitter.complete();
            emitters.remove(userId);
        });

        emitter.onError((e) -> {
            log.warn("SSE Connection error for userId: {}: {}", userId, e.getMessage());
            emitter.complete();
            emitters.remove(userId);
        });

        // Send initial connection notification ping
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to SplitMate Realtime Notification Stream"));
        } catch (IOException e) {
            log.warn("Error sending initial SSE ping to userId {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        }

        return emitter;
    }

    public void sendToUser(UUID userId, NotificationResponse notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                log.info("Pushing realtime SSE notification to userId: {}", userId);
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(notification));
            } catch (IOException e) {
                log.warn("Failed to push SSE notification to userId {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }
}
