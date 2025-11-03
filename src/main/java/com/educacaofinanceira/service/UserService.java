package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateChildRequest;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.*;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;
    private final UserXPRepository userXPRepository;
    private final SavingsRepository savingsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationRepository notificationRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final RedemptionRepository redemptionRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final TransactionRepository transactionRepository;

    // Retorna o usuário autenticado
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return UserResponse.fromUser(user);
    }

    // Cria uma criança (apenas PARENT pode)
    @Transactional
    public UserResponse createChild(CreateChildRequest request) {
        User parent = getAuthenticatedUser();

        // Verifica se o usuário é PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem criar perfis de crianças");
        }

        // Valida se username já existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        // Cria a criança
        User child = new User();
        child.setUsername(request.getUsername());
        child.setPassword(passwordEncoder.encode(request.getPin())); // PIN vira senha
        child.setFullName(request.getFullName());
        child.setRole(UserRole.CHILD);
        child.setFamily(parent.getFamily());
        child.setPin(request.getPin());
        child.setAvatarUrl(request.getAvatarUrl());
        child = userRepository.save(child);

        // Criar Wallet
        Wallet wallet = new Wallet();
        wallet.setChild(child);
        wallet.setBalance(0);
        wallet.setTotalEarned(0);
        wallet.setTotalSpent(0);
        walletRepository.save(wallet);

        // Criar UserXP
        UserXP userXP = new UserXP();
        userXP.setUser(child);
        userXP.setCurrentLevel(1);
        userXP.setCurrentXp(0);
        userXP.setTotalXp(0);
        userXPRepository.save(userXP);

        // Criar Savings
        Savings savings = new Savings();
        savings.setChild(child);
        savings.setBalance(0);
        savings.setTotalDeposited(0);
        savings.setTotalEarned(0);
        savingsRepository.save(savings);

        return UserResponse.fromUser(child);
    }

    // Lista todas as crianças da família (apenas PARENT pode)
    public List<UserResponse> getChildren() {
        User parent = getAuthenticatedUser();

        // Verifica se o usuário é PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem listar crianças");
        }

        // Busca crianças da mesma família
        List<User> children = userRepository.findByFamilyIdAndRole(parent.getFamily().getId(), UserRole.CHILD);

        return children.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    // Deleta uma criança (apenas PARENT pode)
    @Transactional
    public void deleteChild(UUID childId) {
        User parent = getAuthenticatedUser();

        // Verifica se o usuário é PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem deletar perfis de crianças");
        }

        // Busca a criança
        User child = userRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Criança não encontrada"));

        // Valida que é uma criança
        if (child.getRole() != UserRole.CHILD) {
            throw new IllegalArgumentException("Apenas crianças podem ser deletadas");
        }

        // Valida que a criança é da mesma família
        if (!child.getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não pode deletar crianças de outra família");
        }

        // **DELETAR EM ORDEM REVERSA DE DEPENDÊNCIAS**

        // 1. Deletar RefreshTokens
        refreshTokenRepository.deleteByUserId(childId);

        // 2. Deletar Notifications
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(childId);
        notificationRepository.deleteAll(notifications);

        // 3. Deletar UserBadges
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(childId);
        userBadgeRepository.deleteAll(userBadges);

        // 4. Deletar Redemptions
        List<Redemption> redemptions = redemptionRepository.findByChildId(childId);
        redemptionRepository.deleteAll(redemptions);

        // 5. Deletar TaskAssignments
        List<TaskAssignment> taskAssignments = taskAssignmentRepository.findByAssignedToChildId(childId);
        taskAssignmentRepository.deleteAll(taskAssignments);

        // 6. Deletar Transactions (depende do Wallet)
        Wallet wallet = walletRepository.findByChildId(childId).orElse(null);
        if (wallet != null) {
            List<Transaction> transactions = transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
            transactionRepository.deleteAll(transactions);
            walletRepository.delete(wallet);
        }

        // 7. Deletar Savings
        Savings savings = savingsRepository.findByChildId(childId).orElse(null);
        if (savings != null) {
            savingsRepository.delete(savings);
        }

        // 8. Deletar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId).orElse(null);
        if (userXP != null) {
            userXPRepository.delete(userXP);
        }

        // 9. Finalmente, deletar o User
        userRepository.delete(child);
    }

    // Atualiza o avatar do usuário autenticado (PARENT ou CHILD)
    @Transactional
    public UserResponse updateAvatar(String avatarUrl) {
        User user = getAuthenticatedUser();
        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);
        return UserResponse.fromUser(user);
    }

    // Obtém o usuário autenticado
    private User getAuthenticatedUser() {
        String emailOrUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Tenta buscar por email primeiro (PARENT), depois por username (CHILD)
        // USA MÉTODOS COM JOIN FETCH para evitar LazyInitializationException
        return userRepository.findByEmailWithFamily(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsernameWithFamily(emailOrUsername)
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
    }
}
