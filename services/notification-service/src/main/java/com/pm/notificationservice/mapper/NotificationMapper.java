package com.pm.notificationservice.mapper;

import com.pm.notificationservice.dto.response.NotificationResponse;
import com.pm.notificationservice.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponseList(List<Notification> notifications);
}
