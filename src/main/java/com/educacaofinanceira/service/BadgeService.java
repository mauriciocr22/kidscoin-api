package com.educacaofinanceira.service;

import com.educacaofinanceira.model.*;
import com.educacaofinanceira.model.enums.AssignmentStatus;
import com.educacaofinanceira.model.enums.BadgeCriteriaType;
import com.educacaofinanceira.model.enums.RedemptionStatus;
import com.educacaofinanceira.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final WalletRepository walletRepository;
    private final SavingsRepository savingsRepository;
    private final RedemptionRepository redemptionRepository;
    private final UserRepository userRepository;

    /**
     * Verifica e desbloqueia badges para uma criança
     * Retorna lista de badges desbloqueadas nesta verificação
     */
    @Transactional
    public List<Badge> checkAndUnlock(UUID childId) {
        List<Badge> unlockedBadges = new ArrayList<>();
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            // Se já possui esta badge, pular
            if (userBadgeRepository.existsByUserIdAndBadgeId(childId, badge.getId())) {
                continue;
            }

            // Verificar critério baseado no tipo
            boolean criteriaMet = checkCriteria(childId, badge);

            // Se critério atingido, desbloquear
            if (criteriaMet) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(userRepository.findById(childId).orElseThrow());
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
                unlockedBadges.add(badge);
            }
        }

        return unlockedBadges;
    }

    /**
     * Verifica se o critério de uma badge foi atingido
     */
    private boolean checkCriteria(UUID childId, Badge badge) {
        switch (badge.getCriteriaType()) {
            case TASK_COUNT:
                // Total de tarefas aprovadas
                long taskCount = taskAssignmentRepository
                        .countByAssignedToChildIdAndStatus(childId, AssignmentStatus.APPROVED);
                return taskCount >= badge.getCriteriaValue();

            case CURRENT_BALANCE:
                // Saldo atual na carteira
                Wallet wallet = walletRepository.findByChildId(childId).orElse(null);
                return wallet != null && wallet.getBalance() >= badge.getCriteriaValue();

            case TOTAL_COINS_EARNED:
                // Total de moedas ganhas (lifetime)
                Wallet w = walletRepository.findByChildId(childId).orElse(null);
                return w != null && w.getTotalEarned() >= badge.getCriteriaValue();

            case REDEMPTION_COUNT:
                // Total de resgates aprovados
                long redemptionCount = redemptionRepository
                        .countByChildIdAndStatus(childId, RedemptionStatus.APPROVED);
                return redemptionCount >= badge.getCriteriaValue();

            case SAVINGS_AMOUNT:
                // Saldo na poupança
                Savings savings = savingsRepository.findByChildId(childId).orElse(null);
                return savings != null && savings.getBalance() >= badge.getCriteriaValue();

            case TASKS_IN_ONE_DAY:
                // Máximo de tarefas aprovadas em um único dia
                return checkMaxTasksInOneDay(childId, badge.getCriteriaValue());

            case STREAK_DAYS:
                // Dias consecutivos com tarefas aprovadas
                return checkStreakDays(childId, badge.getCriteriaValue());

            case DAYS_SAVED:
                // Dias consecutivos com dinheiro na poupança
                Savings s = savingsRepository.findByChildId(childId).orElse(null);
                if (s != null && s.getLastDepositAt() != null && s.getBalance() > 0) {
                    long days = ChronoUnit.DAYS.between(s.getLastDepositAt(), LocalDateTime.now());
                    return days >= badge.getCriteriaValue();
                }
                return false;

            default:
                return false;
        }
    }

    /**
     * Verifica se a criança completou X tarefas em um único dia
     */
    private boolean checkMaxTasksInOneDay(UUID childId, Integer targetCount) {
        // Buscar todas as tarefas aprovadas
        List<TaskAssignment> approvedTasks = taskAssignmentRepository
                .findByAssignedToChildIdAndStatusOrderByApprovedAtDesc(childId, AssignmentStatus.APPROVED);

        // Agrupar por data e contar
        int maxInOneDay = 0;
        LocalDate currentDate = null;
        int currentCount = 0;

        for (TaskAssignment task : approvedTasks) {
            if (task.getApprovedAt() == null) continue;

            LocalDate taskDate = task.getApprovedAt().toLocalDate();

            if (currentDate == null || !taskDate.equals(currentDate)) {
                // Nova data
                currentDate = taskDate;
                currentCount = 1;
            } else {
                // Mesma data
                currentCount++;
            }

            maxInOneDay = Math.max(maxInOneDay, currentCount);
        }

        return maxInOneDay >= targetCount;
    }

    /**
     * Verifica se a criança tem uma sequência de X dias consecutivos com tarefas
     */
    private boolean checkStreakDays(UUID childId, Integer targetDays) {
        // Buscar tarefas aprovadas nos últimos 60 dias
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
        List<TaskAssignment> approvedTasks = taskAssignmentRepository
                .findByAssignedToChildIdAndStatusAndApprovedAtBetween(
                        childId, AssignmentStatus.APPROVED, sixtyDaysAgo, LocalDateTime.now());

        if (approvedTasks.isEmpty()) return false;

        // Extrair datas únicas (dias com pelo menos 1 tarefa aprovada)
        List<LocalDate> dates = approvedTasks.stream()
                .map(t -> t.getApprovedAt().toLocalDate())
                .distinct()
                .sorted()
                .toList();

        // Verificar maior sequência consecutiva
        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < dates.size(); i++) {
            LocalDate prevDate = dates.get(i - 1);
            LocalDate currDate = dates.get(i);

            // Se é o dia seguinte, incrementar streak
            if (ChronoUnit.DAYS.between(prevDate, currDate) == 1) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                // Quebrou o streak
                currentStreak = 1;
            }
        }

        return maxStreak >= targetDays;
    }
}
