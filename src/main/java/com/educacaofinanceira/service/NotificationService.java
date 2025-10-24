package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.NotificationResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.model.Notification;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.repository.NotificationRepository;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Cria uma nova notificação para o usuário
     */
    @Transactional
    public void create(UUID userId, NotificationType type, String title,
                      String message, ReferenceType referenceType, UUID referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    /**
     * Busca todas as notificações de um usuário (ordenadas por data)
     */
    public List<NotificationResponse> getUserNotifications(UUID userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(NotificationResponse::fromNotification)
                .collect(Collectors.toList());
    }

    /**
     * Marca uma notificação como lida
     */
    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Marca todas as notificações de um usuário como lidas
     */
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(notification -> {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setReadAt(now);
            }
        });

        notificationRepository.saveAll(notifications);
    }

    /**
     * Conta notificações não lidas de um usuário
     */
    public Long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
}
