package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAvatarRequest {

    @NotBlank(message = "URL do avatar é obrigatória")
    @Size(max = 255, message = "URL do avatar deve ter no máximo 255 caracteres")
    private String avatarUrl;
}
