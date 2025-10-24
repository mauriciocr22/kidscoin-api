package com.educacaofinanceira.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateRewardRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Custo em moedas é obrigatório")
    @Positive(message = "Custo em moedas deve ser positivo")
    private Integer coinCost;

    private String category;

    private String imageUrl;
}
