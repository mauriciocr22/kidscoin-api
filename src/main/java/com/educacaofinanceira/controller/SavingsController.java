package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.DepositSavingsRequest;
import com.educacaofinanceira.dto.request.WithdrawSavingsRequest;
import com.educacaofinanceira.dto.response.SavingsResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.SavingsService;
import com.educacaofinanceira.util.SecurityHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsService savingsService;
    private final SecurityHelper securityHelper;

    /**
     * Deposita moedas na poupança
     * Query param: childId (opcional - se não fornecido, usa o próprio usuário)
     */
    @PostMapping("/deposit")
    public ResponseEntity<SavingsResponse> deposit(
            @Valid @RequestBody DepositSavingsRequest request,
            @RequestParam(required = false) UUID childId) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        SavingsResponse savings = savingsService.deposit(targetChildId, request.getAmount(), user);
        return ResponseEntity.ok(savings);
    }

    /**
     * Saca moedas da poupança (com bônus por tempo)
     * Query param: childId (opcional - se não fornecido, usa o próprio usuário)
     */
    @PostMapping("/withdraw")
    public ResponseEntity<SavingsResponse> withdraw(
            @Valid @RequestBody WithdrawSavingsRequest request,
            @RequestParam(required = false) UUID childId) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        SavingsResponse savings = savingsService.withdraw(targetChildId, request.getAmount(), user);
        return ResponseEntity.ok(savings);
    }

    /**
     * Busca dados da poupança
     * Query param: childId (opcional - se não fornecido, usa o próprio usuário)
     */
    @GetMapping
    public ResponseEntity<SavingsResponse> getSavings(@RequestParam(required = false) UUID childId) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        SavingsResponse savings = savingsService.getSavings(targetChildId, user);
        return ResponseEntity.ok(savings);
    }
}
