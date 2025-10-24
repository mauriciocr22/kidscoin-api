package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Reward;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RewardResponse {

    private UUID id;
    private String name;
    private String description;
    private Integer coinCost;
    private String category;
    private String imageUrl;
    private Boolean isActive;
    private UUID familyId;
    private String createdByName;
    private LocalDateTime createdAt;

    public static RewardResponse fromReward(Reward reward) {
        RewardResponse response = new RewardResponse();
        response.setId(reward.getId());
        response.setName(reward.getName());
        response.setDescription(reward.getDescription());
        response.setCoinCost(reward.getCoinCost());
        response.setCategory(reward.getCategory());
        response.setImageUrl(reward.getImageUrl());
        response.setIsActive(reward.getIsActive());
        response.setFamilyId(reward.getFamily().getId());
        response.setCreatedByName(reward.getCreatedBy().getFullName());
        response.setCreatedAt(reward.getCreatedAt());
        return response;
    }
}
