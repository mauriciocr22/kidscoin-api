package com.educacaofinanceira.dto.request;

import com.educacaofinanceira.model.enums.TaskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Valor em moedas é obrigatório")
    @Positive(message = "Valor em moedas deve ser positivo")
    private Integer coinValue;

    @NotNull(message = "Valor de XP é obrigatório")
    @Positive(message = "Valor de XP deve ser positivo")
    private Integer xpValue;

    @NotNull(message = "Categoria é obrigatória")
    private TaskCategory category;

    @NotEmpty(message = "Deve atribuir a pelo menos uma criança")
    private List<UUID> childrenIds;
}
