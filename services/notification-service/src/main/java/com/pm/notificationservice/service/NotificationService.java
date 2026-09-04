package com.pm.notificationservice.service;

import com.pm.notificationservice.dto.request.SendReminderRequest;
import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.dto.response.UnreadCountResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(UUID userId);
    UnreadCountResponse getUnreadCount(UUID userId);
    void markAsRead(UUID userId, UUID notificationId);
    void markAllAsRead(UUID userId);
    NotificationResponse sendReminder(UUID creditorId, SendReminderRequest request);
    void createAndSendNotification(UUID recipientId, UUID senderId, String title, String content, String type, UUID referenceId);
}
