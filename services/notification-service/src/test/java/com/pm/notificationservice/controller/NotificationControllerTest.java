package com.pm.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.notificationservice.dto.request.SendReminderRequest;
import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.dto.response.UnreadCountResponse;
import com.pm.notificationservice.exception.GlobalExceptionHandler;
import com.pm.notificationservice.service.NotificationService;
import com.pm.notificationservice.service.SseEmitterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private NotificationService notificationService;

    @Mock
    private SseEmitterService sseEmitterService;

    @InjectMocks
    private NotificationController notificationController;

    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count should return count")
    void getUnreadCount_shouldReturn200() throws Exception {
        UnreadCountResponse response = UnreadCountResponse.builder().unreadCount(3L).build();

        when(notificationService.getUnreadCount(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(3));
    }

    @Test
    @DisplayName("POST /api/v1/notifications/remind should send reminder")
    void sendReminder_shouldReturn201() throws Exception {
        SendReminderRequest request = SendReminderRequest.builder()
                .debtorId(UUID.randomUUID())
                .amount(new BigDecimal("150000.00"))
                .build();

        NotificationResponse response = NotificationResponse.builder()
                .id(UUID.randomUUID())
                .title("Payment Reminder")
                .type("PAYMENT_REMINDER")
                .build();

        when(notificationService.sendReminder(any(), any(SendReminderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/notifications/remind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Payment Reminder"));
    }
}
