package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Badge;
import com.educacaofinanceira.model.enums.BadgeCriteriaType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BadgeResponse {

    private UUID id;
    private String name;
    private String description;
    private String iconName;
    private BadgeCriteriaType criteriaType;
    private Integer criteriaValue;
    private Integer xpBonus;
    private Boolean unlocked;
    private LocalDateTime unlockedAt;

    public static BadgeResponse fromBadge(Badge badge, Boolean unlocked, LocalDateTime unlockedAt) {
        BadgeResponse response = new BadgeResponse();
        response.setId(badge.getId());
        response.setName(badge.getName());
        response.setDescription(badge.getDescription());
        response.setIconName(badge.getIconName());
        response.setCriteriaType(badge.getCriteriaType());
        response.setCriteriaValue(badge.getCriteriaValue());
        response.setXpBonus(badge.getXpBonus());
        response.setUnlocked(unlocked);
        response.setUnlockedAt(unlockedAt);
        return response;
    }
}
