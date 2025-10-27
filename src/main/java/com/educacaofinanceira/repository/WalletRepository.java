package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    /**
     * Busca carteira por ID da criança
     * SEM JOIN FETCH - use findByChildIdWithChild() se precisar acessar child
     */
    Optional<Wallet> findByChildId(UUID childId);

    /**
     * Busca carteira com JOIN FETCH do child
     * Evita LazyInitializationException ao acessar wallet.getChild()
     */
    @Query("SELECT w FROM Wallet w JOIN FETCH w.child WHERE w.child.id = :childId")
    Optional<Wallet> findByChildIdWithChild(@Param("childId") UUID childId);

    // Lock pessimista para evitar concorrência
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.child.id = :childId")
    Optional<Wallet> findByChildIdWithLock(@Param("childId") UUID childId);
}
