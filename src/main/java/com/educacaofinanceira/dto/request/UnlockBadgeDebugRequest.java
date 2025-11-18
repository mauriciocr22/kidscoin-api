package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 🔧 DTO para desbloquear badges manualmente (DEBUG)
 * ⚠️ REMOVER ANTES DE PRODUÇÃO!
 */
@Data
public class UnlockBadgeDebugRequest {

    @NotBlank(message = "Username da criança é obrigatório")
    private String username;

    @NotBlank(message = "Nome da badge é obrigatório")
    private String badgeName;
}
