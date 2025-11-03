package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.CreateChildRequest;
import com.educacaofinanceira.dto.request.UpdateAvatarRequest;
import com.educacaofinanceira.dto.response.UserResponse;
import com.educacaofinanceira.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/children")
    public ResponseEntity<UserResponse> createChild(@Valid @RequestBody CreateChildRequest request) {
        UserResponse child = userService.createChild(request);
        return ResponseEntity.ok(child);
    }

    @GetMapping("/children")
    public ResponseEntity<List<UserResponse>> getChildren() {
        List<UserResponse> children = userService.getChildren();
        return ResponseEntity.ok(children);
    }

    /**
     * Deleta uma criança e todos os dados relacionados (apenas PARENT)
     * Deleta: RefreshTokens, Notifications, UserBadges, Redemptions,
     * TaskAssignments, Transactions, Wallet, Savings, UserXP e User
     */
    @DeleteMapping("/children/{childId}")
    public ResponseEntity<Void> deleteChild(@PathVariable UUID childId) {
        userService.deleteChild(childId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza o avatar do usuário autenticado (PARENT ou CHILD)
     */
    @PatchMapping("/avatar")
    public ResponseEntity<UserResponse> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
        UserResponse user = userService.updateAvatar(request.getAvatarUrl());
        return ResponseEntity.ok(user);
    }
}
