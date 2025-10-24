package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectRedemptionRequest {

    @NotBlank(message = "Motivo da rejeição é obrigatório")
    private String rejectionReason;
}
