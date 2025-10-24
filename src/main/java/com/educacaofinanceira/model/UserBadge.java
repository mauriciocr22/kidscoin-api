package com.educacaofinanceira.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_badges", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "badge_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User CHILD

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    @PrePersist
    protected void onCreate() {
        unlockedAt = LocalDateTime.now();
    }
}
