package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByFamilyIdAndRole(UUID familyId, UserRole role);
}
