package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.CreateRedemptionRequest;
import com.educacaofinanceira.dto.request.RejectRedemptionRequest;
import com.educacaofinanceira.dto.response.RedemptionResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.RedemptionStatus;
import com.educacaofinanceira.service.RedemptionService;
import com.educacaofinanceira.util.SecurityHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/redemptions")
@RequiredArgsConstructor
public class RedemptionController {

    private final RedemptionService redemptionService;
    private final SecurityHelper securityHelper;

    /**
     * Solicita resgate de recompensa (apenas CHILD)
     */
    @PostMapping
    public ResponseEntity<RedemptionResponse> requestRedemption(
            @Valid @RequestBody CreateRedemptionRequest request) {
        User child = securityHelper.getAuthenticatedUser();
        RedemptionResponse redemption = redemptionService.requestRedemption(request, child);
        return ResponseEntity.ok(redemption);
    }

    /**
     * Lista resgates
     * Query param: status (opcional - PENDING, APPROVED, REJECTED)
     */
    @GetMapping
    public ResponseEntity<List<RedemptionResponse>> getRedemptions(
            @RequestParam(required = false) RedemptionStatus status) {
        User user = securityHelper.getAuthenticatedUser();
        List<RedemptionResponse> redemptions = redemptionService.getRedemptions(user, status);
        return ResponseEntity.ok(redemptions);
    }

    /**
     * Aprova resgate (apenas PARENT)
     */
    @PostMapping("/{redemptionId}/approve")
    public ResponseEntity<RedemptionResponse> approveRedemption(@PathVariable UUID redemptionId) {
        User parent = securityHelper.getAuthenticatedUser();
        RedemptionResponse redemption = redemptionService.approveRedemption(redemptionId, parent);
        return ResponseEntity.ok(redemption);
    }

    /**
     * Rejeita resgate (apenas PARENT)
     */
    @PostMapping("/{redemptionId}/reject")
    public ResponseEntity<RedemptionResponse> rejectRedemption(
            @PathVariable UUID redemptionId,
            @Valid @RequestBody RejectRedemptionRequest request) {
        User parent = securityHelper.getAuthenticatedUser();
        RedemptionResponse redemption = redemptionService.rejectRedemption(
                redemptionId, request.getRejectionReason(), parent);
        return ResponseEntity.ok(redemption);
    }
}
