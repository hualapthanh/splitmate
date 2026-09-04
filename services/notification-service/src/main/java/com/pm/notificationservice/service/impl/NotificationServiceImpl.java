package com.pm.notificationservice.service.impl;

import com.pm.notificationservice.dto.request.SendReminderRequest;
import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.dto.response.UnreadCountResponse;
import com.pm.notificationservice.entity.Notification;
import com.pm.notificationservice.exception.BusinessException;
import com.pm.notificationservice.exception.ErrorCode;
import com.pm.notificationservice.exception.ResourceNotFoundException;
import com.pm.notificationservice.mapper.NotificationMapper;
import com.pm.notificationservice.repository.NotificationRepository;
import com.pm.notificationservice.service.NotificationService;
import com.pm.notificationservice.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final SseEmitterService sseEmitterService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notificationMapper.toNotificationResponseList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(UUID userId) {
        long count = notificationRepository.countByRecipientIdAndIsReadFalse(userId);
        return UnreadCountResponse.builder().unreadCount(count).build();
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getRecipientId().equals(userId)) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Notification does not belong to user");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @Transactional
    public NotificationResponse sendReminder(UUID creditorId, SendReminderRequest request) {
        if (creditorId.equals(request.getDebtorId())) {
            throw new BusinessException(ErrorCode.NOTIF_002, "Cannot send payment reminder to yourself");
        }

        String title = "Payment Reminder";
        String content = String.format("A member has sent you a reminder to settle your debt of %s VND", request.getAmount());

        Notification notification = Notification.builder()
                .recipientId(request.getDebtorId())
                .senderId(creditorId)
                .title(title)
                .content(content)
                .type("PAYMENT_REMINDER")
                .referenceId(request.getGroupId())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toNotificationResponse(saved);

        // Push realtime SSE
        sseEmitterService.sendToUser(request.getDebtorId(), response);

        log.info("Sent payment reminder from creditor {} to debtor {}", creditorId, request.getDebtorId());
        return response;
    }

    @Override
    @Transactional
    public void createAndSendNotification(UUID recipientId, UUID senderId, String title, String content, String type, UUID referenceId) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .senderId(senderId)
                .title(title)
                .content(content)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.toNotificationResponse(saved);

        // Push realtime SSE
        sseEmitterService.sendToUser(recipientId, response);
    }
}
