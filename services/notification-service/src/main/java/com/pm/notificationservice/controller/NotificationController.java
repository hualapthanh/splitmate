package com.pm.notificationservice.controller;

import com.pm.notificationservice.dto.request.SendReminderRequest;
import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.dto.response.UnreadCountResponse;
import com.pm.notificationservice.security.UserPrincipal;
import com.pm.notificationservice.security.annotation.CurrentUser;
import com.pm.notificationservice.service.NotificationService;
import com.pm.notificationservice.service.SseEmitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Endpoints for SSE realtime streaming, in-app notifications, unread counters, and debt payment reminders")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to SSE Realtime Notifications", description = "Establishes a Server-Sent Events (SSE) stream for live pushing of notifications to Web/App clients.")
    public SseEmitter subscribe(@CurrentUser UserPrincipal principal) {
        return sseEmitterService.subscribe(principal.getUserId());
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user's notifications", description = "Returns list of in-app notifications for the authenticated user ordered by newest first.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public List<NotificationResponse> getUserNotifications(@CurrentUser UserPrincipal principal) {
        return notificationService.getUserNotifications(principal.getUserId());
    }

    @GetMapping("/unread-count")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get unread count", description = "Returns total count of unread notifications for badge counters.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully")
    })
    public UnreadCountResponse getUnreadCount(@CurrentUser UserPrincipal principal) {
        return notificationService.getUnreadCount(principal.getUserId());
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read by ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public void markAsRead(
            @CurrentUser UserPrincipal principal,
            @PathVariable("id") UUID notificationId
    ) {
        notificationService.markAsRead(principal.getUserId(), notificationId);
    }

    @PutMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications for the authenticated user as read.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All notifications marked as read successfully")
    })
    public void markAllAsRead(@CurrentUser UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getUserId());
    }

    @PostMapping("/remind")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send payment reminder", description = "Triggers a payment reminder notification to a debtor. Pushes via realtime SSE.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Payment reminder sent successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot send reminder to self")
    })
    public NotificationResponse sendReminder(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody SendReminderRequest request
    ) {
        return notificationService.sendReminder(principal.getUserId(), request);
    }
}
