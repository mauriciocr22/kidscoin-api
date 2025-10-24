package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.BadgeResponse;
import com.educacaofinanceira.dto.response.GamificationResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.model.Badge;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.UserBadge;
import com.educacaofinanceira.model.UserXP;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.repository.BadgeRepository;
import com.educacaofinanceira.repository.UserBadgeRepository;
import com.educacaofinanceira.repository.UserXPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Classe auxiliar para retornar resultado da gamificação
 */
class GamificationResult {
    boolean leveledUp;
    int newLevel;
    List<Badge> unlockedBadges;

    GamificationResult(boolean leveledUp, int newLevel, List<Badge> unlockedBadges) {
        this.leveledUp = leveledUp;
        this.newLevel = newLevel;
        this.unlockedBadges = unlockedBadges;
    }
}

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserXPRepository userXPRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeService badgeService;
    private final NotificationService notificationService;

    /**
     * Adiciona XP para uma criança e verifica level up e badges
     */
    @Transactional
    public GamificationResult addXP(UUID childId, Integer xpAmount, String reason) {
        // Buscar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        // Adicionar XP
        userXP.setTotalXp(userXP.getTotalXp() + xpAmount);

        // Verificar subida de nível
        boolean leveledUp = false;
        int newLevel = userXP.getCurrentLevel();

        while (newLevel < 10) {
            int xpNeededForNextLevel = calculateXPForLevel(newLevel + 1);
            if (userXP.getTotalXp() >= xpNeededForNextLevel) {
                newLevel++;
                userXP.setCurrentLevel(newLevel);
                userXP.setLastLevelUpAt(LocalDateTime.now());
                leveledUp = true;
            } else {
                break;
            }
        }

        // Calcular currentXp (XP no nível atual)
        int xpForCurrentLevel = calculateXPForLevel(newLevel);
        userXP.setCurrentXp(userXP.getTotalXp() - xpForCurrentLevel);

        userXPRepository.save(userXP);

        // Verificar badges
        List<Badge> unlockedBadges = badgeService.checkAndUnlock(childId);

        // Se desbloqueou badges, adicionar XP bônus (recursivo)
        if (!unlockedBadges.isEmpty()) {
            int bonusXP = unlockedBadges.stream()
                    .mapToInt(Badge::getXpBonus)
                    .sum();

            if (bonusXP > 0) {
                // Chamada recursiva com XP bônus
                return addXP(childId, bonusXP, "Bônus de badges");
            }
        }

        // Criar notificações
        if (leveledUp) {
            notificationService.create(childId, NotificationType.LEVEL_UP,
                    "Subiu de nível!",
                    "Parabéns! Você chegou ao nível " + newLevel,
                    null, null);
        }

        for (Badge badge : unlockedBadges) {
            notificationService.create(childId, NotificationType.BADGE_UNLOCKED,
                    "Nova conquista!",
                    "Você desbloqueou: " + badge.getName(),
                    ReferenceType.TASK, badge.getId());
        }

        return new GamificationResult(leveledUp, newLevel, unlockedBadges);
    }

    /**
     * Calcula XP total necessário para atingir um nível
     * Fórmula: Para cada nível i (de 1 até level): i * 100 + (i-1) * 50
     */
    public int calculateXPForLevel(int level) {
        if (level <= 1) return 0;

        int totalXP = 0;
        for (int i = 1; i < level; i++) {
            totalXP += i * 100 + (i - 1) * 50;
        }
        return totalXP;
    }

    /**
     * Busca dados de gamificação de uma criança
     */
    public GamificationResponse getGamification(UUID childId, User requestingUser) {
        // Buscar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        // Calcular XP necessário para próximo nível
        int xpForNextLevel;
        if (userXP.getCurrentLevel() >= 10) {
            xpForNextLevel = userXP.getTotalXp(); // Nível máximo
        } else {
            xpForNextLevel = calculateXPForLevel(userXP.getCurrentLevel() + 1);
        }

        // Buscar badges desbloqueadas
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(childId);
        Map<UUID, LocalDateTime> unlockedBadgesMap = userBadges.stream()
                .collect(Collectors.toMap(
                        ub -> ub.getBadge().getId(),
                        UserBadge::getUnlockedAt
                ));

        // Buscar todas as badges e marcar quais estão desbloqueadas
        List<Badge> allBadges = badgeRepository.findAll();
        List<BadgeResponse> badgeResponses = allBadges.stream()
                .map(badge -> {
                    boolean unlocked = unlockedBadgesMap.containsKey(badge.getId());
                    LocalDateTime unlockedAt = unlocked ? unlockedBadgesMap.get(badge.getId()) : null;
                    return BadgeResponse.fromBadge(badge, unlocked, unlockedAt);
                })
                .collect(Collectors.toList());

        return new GamificationResponse(
                userXP.getCurrentLevel(),
                userXP.getCurrentXp(),
                userXP.getTotalXp(),
                xpForNextLevel,
                badgeResponses
        );
    }
}
