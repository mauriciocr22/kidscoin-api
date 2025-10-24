package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.TransactionResponse;
import com.educacaofinanceira.dto.response.WalletResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Transaction;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.Wallet;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.TransactionType;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.TransactionRepository;
import com.educacaofinanceira.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Credita moedas na carteira da criança
     * Usa lock pessimista para evitar race conditions
     */
    @Transactional
    public Integer credit(UUID childId, Integer amount, String description,
                         ReferenceType referenceType, UUID referenceId) {
        // Buscar wallet com lock
        Wallet wallet = walletRepository.findByChildIdWithLock(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        // Calcular balances
        Integer balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setTotalEarned(wallet.getTotalEarned() + amount);
        Integer balanceAfter = wallet.getBalance();

        // Criar transação
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);

        // Salvar
        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        return balanceAfter;
    }

    /**
     * Debita moedas da carteira da criança
     * Valida se há saldo suficiente
     */
    @Transactional
    public Integer debit(UUID childId, Integer amount, String description,
                        ReferenceType referenceType, UUID referenceId) {
        // Buscar wallet com lock
        Wallet wallet = walletRepository.findByChildIdWithLock(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        // Validar saldo
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException("Saldo insuficiente. Saldo atual: " +
                    wallet.getBalance() + " moedas");
        }

        // Calcular balances
        Integer balanceBefore = wallet.getBalance();
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setTotalSpent(wallet.getTotalSpent() + amount);
        Integer balanceAfter = wallet.getBalance();

        // Criar transação
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);

        // Salvar
        walletRepository.save(wallet);
        transactionRepository.save(transaction);

        return balanceAfter;
    }

    /**
     * Busca a carteira de uma criança
     * Valida acesso: pai da família ou própria criança
     */
    public WalletResponse getWallet(UUID childId, User requestingUser) {
        Wallet wallet = walletRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        // Validar acesso
        validateAccess(wallet.getChild(), requestingUser);

        return WalletResponse.fromWallet(wallet);
    }

    /**
     * Busca o histórico de transações de uma criança
     * Com paginação
     */
    public List<TransactionResponse> getTransactions(UUID childId, User requestingUser,
                                                     Integer limit, Integer offset) {
        Wallet wallet = walletRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        // Validar acesso
        validateAccess(wallet.getChild(), requestingUser);

        // Buscar transações com paginação
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Transaction> transactions = transactionRepository
                .findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);

        return transactions.stream()
                .map(TransactionResponse::fromTransaction)
                .collect(Collectors.toList());
    }

    /**
     * Valida se o usuário tem acesso aos dados da criança
     * - PARENT: deve ser da mesma família
     * - CHILD: deve ser a própria criança
     */
    private void validateAccess(User child, User requestingUser) {
        if (requestingUser.getRole() == UserRole.PARENT) {
            // Pai deve ser da mesma família
            if (!child.getFamily().getId().equals(requestingUser.getFamily().getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta carteira");
            }
        } else {
            // Criança só pode ver a própria carteira
            if (!child.getId().equals(requestingUser.getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta carteira");
            }
        }
    }
}
