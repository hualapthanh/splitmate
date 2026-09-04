package com.pm.notificationservice.service;

import com.pm.notificationservice.dto.request.SendReminderRequest;
import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.dto.response.UnreadCountResponse;
import com.pm.notificationservice.entity.Notification;
import com.pm.notificationservice.exception.BusinessException;
import com.pm.notificationservice.mapper.NotificationMapper;
import com.pm.notificationservice.repository.NotificationRepository;
import com.pm.notificationservice.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private SseEmitterService sseEmitterService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID sampleCreditorId;
    private UUID sampleDebtorId;
    private UUID sampleGroupId;

    @BeforeEach
    void setUp() {
        sampleCreditorId = UUID.randomUUID();
        sampleDebtorId = UUID.randomUUID();
        sampleGroupId = UUID.randomUUID();
    }

    @Test
    @DisplayName("sendReminder() should save notification, push SSE, and return response")
    void sendReminder_success() {
        SendReminderRequest request = SendReminderRequest.builder()
                .debtorId(sampleDebtorId)
                .groupId(sampleGroupId)
                .amount(new BigDecimal("150000.00"))
                .build();

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .recipientId(sampleDebtorId)
                .senderId(sampleCreditorId)
                .title("Payment Reminder")
                .type("PAYMENT_REMINDER")
                .build();

        NotificationResponse expectedResponse = NotificationResponse.builder()
                .id(notification.getId())
                .title("Payment Reminder")
                .type("PAYMENT_REMINDER")
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toNotificationResponse(notification)).thenReturn(expectedResponse);

        NotificationResponse response = notificationService.sendReminder(sampleCreditorId, request);

        assertNotNull(response);
        assertEquals("Payment Reminder", response.getTitle());
        verify(notificationRepository).save(any(Notification.class));
        verify(sseEmitterService).sendToUser(eq(sampleDebtorId), any());
    }

    @Test
    @DisplayName("sendReminder() should throw BusinessException when creditor equals debtor")
    void sendReminder_sameUser_throwsException() {
        SendReminderRequest request = SendReminderRequest.builder()
                .debtorId(sampleCreditorId)
                .amount(new BigDecimal("150000.00"))
                .build();

        assertThrows(BusinessException.class, () -> notificationService.sendReminder(sampleCreditorId, request));
    }

    @Test
    @DisplayName("getUnreadCount() should return correct unread count")
    void getUnreadCount_success() {
        when(notificationRepository.countByRecipientIdAndIsReadFalse(sampleCreditorId)).thenReturn(5L);

        UnreadCountResponse response = notificationService.getUnreadCount(sampleCreditorId);

        assertNotNull(response);
        assertEquals(5L, response.getUnreadCount());
    }
}
