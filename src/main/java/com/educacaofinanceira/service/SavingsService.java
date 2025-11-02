package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.SavingsResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Savings;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.SavingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsService {

    private final SavingsRepository savingsRepository;
    private final WalletService walletService;
    private final NotificationService notificationService;

    /**
     * Deposita moedas da carteira para a poupança
     */
    @Transactional
    public SavingsResponse deposit(UUID childId, Integer amount, User requestingUser) {
        // Validar acesso
        validateAccess(childId, requestingUser);

        // Debitar da carteira
        walletService.debit(childId, amount,
                "Depósito na poupança",
                ReferenceType.SAVINGS, null);

        // Creditar na poupança
        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        savings.setBalance(savings.getBalance() + amount);
        savings.setTotalDeposited(savings.getTotalDeposited() + amount);
        savings.setLastDepositAt(LocalDateTime.now());
        savings = savingsRepository.save(savings);

        // Notificar criança
        notificationService.create(childId, NotificationType.SAVINGS_DEPOSIT,
                "Depósito realizado",
                "Você depositou " + amount + " moedas na poupança!",
                ReferenceType.SAVINGS, savings.getId());

        return SavingsResponse.fromSavings(savings);
    }

    /**
     * Saca moedas da poupança para a carteira
     * Aplica bônus por tempo guardado:
     * - < 7 dias: 0%
     * - 7-29 dias: +2%
     * - 30+ dias: +10%
     */
    @Transactional
    public SavingsResponse withdraw(UUID childId, Integer amount, User requestingUser) {
        // Validar acesso
        validateAccess(childId, requestingUser);

        // Buscar poupança
        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        // Validar saldo
        if (savings.getBalance() < amount) {
            throw new IllegalArgumentException("Saldo insuficiente na poupança. Saldo atual: " +
                    savings.getBalance() + " moedas");
        }

        // Calcular bônus por tempo guardado
        Integer bonus = calculateTimeBonus(savings, amount);
        Integer totalAmount = amount + bonus;

        // Debitar da poupança
        savings.setBalance(savings.getBalance() - amount);
        savings = savingsRepository.save(savings);

        // Creditar na carteira (valor + bônus)
        walletService.credit(childId, totalAmount,
                "Saque da poupança" + (bonus > 0 ? " (+" + bonus + " de bônus)" : ""),
                ReferenceType.SAVINGS, savings.getId());

        // Notificar criança
        String message = "Você sacou " + amount + " moedas da poupança";
        if (bonus > 0) {
            message += " e ganhou " + bonus + " moedas de bônus!";
        }

        notificationService.create(childId, NotificationType.SAVINGS_WITHDRAWAL,
                "Saque realizado",
                message,
                ReferenceType.SAVINGS, savings.getId());

        return SavingsResponse.fromSavings(savings);
    }

    /**
     * Busca dados da poupança
     */
    @Transactional(readOnly = true)
    public SavingsResponse getSavings(UUID childId, User requestingUser) {
        validateAccess(childId, requestingUser);

        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        return SavingsResponse.fromSavings(savings);
    }

    /**
     * Aplica rendimento semanal de 2%
     * Executado automaticamente todo domingo à meia-noite
     */
    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void applyWeeklyInterest() {
        log.info("Aplicando rendimento semanal nas poupanças...");

        List<Savings> savingsWithBalance = savingsRepository.findAllByBalanceGreaterThan(0);

        for (Savings savings : savingsWithBalance) {
            // Calcular 2% de rendimento
            Integer interest = (int) Math.ceil(savings.getBalance() * 0.02);

            // Adicionar ao saldo e total earned
            savings.setBalance(savings.getBalance() + interest);
            savings.setTotalEarned(savings.getTotalEarned() + interest);
            savingsRepository.save(savings);

            // Notificar criança
            notificationService.create(savings.getChild().getId(),
                    NotificationType.SAVINGS_INTEREST,
                    "Rendimento semanal",
                    "Sua poupança rendeu " + interest + " moedas esta semana!",
                    ReferenceType.SAVINGS, savings.getId());

            log.info("Rendimento aplicado para child_id={}: {} moedas",
                    savings.getChild().getId(), interest);
        }

        log.info("Rendimento semanal aplicado em {} poupanças", savingsWithBalance.size());
    }

    /**
     * Calcula bônus por tempo guardado
     * - < 7 dias: 0%
     * - 7-29 dias: +2%
     * - 30+ dias: +10%
     */
    private Integer calculateTimeBonus(Savings savings, Integer amount) {
        if (savings.getLastDepositAt() == null) {
            return 0;
        }

        long daysStored = ChronoUnit.DAYS.between(savings.getLastDepositAt(), LocalDateTime.now());

        if (daysStored >= 30) {
            return (int) Math.ceil(amount * 0.10); // 10%
        } else if (daysStored >= 7) {
            return (int) Math.ceil(amount * 0.02); // 2%
        } else {
            return 0; // Sem bônus
        }
    }

    /**
     * Valida acesso (PARENT da família ou própria CHILD)
     */
    private void validateAccess(UUID childId, User requestingUser) {
        if (requestingUser.getRole() == UserRole.PARENT) {
            // Pai deve ser da mesma família
            Savings savings = savingsRepository.findByChildId(childId)
                    .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

            if (!savings.getChild().getFamily().getId().equals(requestingUser.getFamily().getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta poupança");
            }
        } else {
            // Criança só pode acessar própria poupança
            if (!childId.equals(requestingUser.getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta poupança");
            }
        }
    }
}
