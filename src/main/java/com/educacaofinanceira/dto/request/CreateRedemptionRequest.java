package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateRedemptionRequest {

    @NotNull(message = "ID da recompensa é obrigatório")
    private UUID rewardId;
}
