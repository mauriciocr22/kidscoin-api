package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RewardRepository extends JpaRepository<Reward, UUID> {

    List<Reward> findByFamilyId(UUID familyId);

    List<Reward> findByFamilyIdAndIsActive(UUID familyId, Boolean isActive);

    /**
     * Busca recompensas por família com EAGER fetch de relacionamentos
     * Evita LazyInitializationException ao serializar para JSON
     * JOIN FETCH em cascata: Reward -> Family, Reward -> CreatedBy -> Family
     */
    @Query("SELECT r FROM Reward r " +
           "JOIN FETCH r.family " +
           "JOIN FETCH r.createdBy cb " +
           "JOIN FETCH cb.family " +
           "WHERE r.family.id = :familyId")
    List<Reward> findByFamilyIdWithRelations(@Param("familyId") UUID familyId);

    /**
     * Busca recompensas ativas por família com EAGER fetch de relacionamentos
     * Evita LazyInitializationException ao serializar para JSON
     * JOIN FETCH em cascata: Reward -> Family, Reward -> CreatedBy -> Family
     */
    @Query("SELECT r FROM Reward r " +
           "JOIN FETCH r.family " +
           "JOIN FETCH r.createdBy cb " +
           "JOIN FETCH cb.family " +
           "WHERE r.family.id = :familyId AND r.isActive = :isActive")
    List<Reward> findByFamilyIdAndIsActiveWithRelations(@Param("familyId") UUID familyId,
                                                         @Param("isActive") Boolean isActive);
}
