package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Notification;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private ReferenceType referenceType;
    private UUID referenceId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResponse fromNotification(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setReferenceType(notification.getReferenceType());
        response.setReferenceId(notification.getReferenceId());
        response.setIsRead(notification.getIsRead());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
