package com.educacaofinanceira.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GamificationResponse {

    private Integer currentLevel;
    private Integer currentXp;
    private Integer totalXp;
    private Integer xpForNextLevel;
    private Integer xpNeededForNextLevel;
    private List<BadgeResponse> badges;

    public GamificationResponse(Integer currentLevel, Integer currentXp, Integer totalXp,
                                Integer xpForNextLevel, List<BadgeResponse> badges) {
        this.currentLevel = currentLevel;
        this.currentXp = currentXp;
        this.totalXp = totalXp;
        this.xpForNextLevel = xpForNextLevel;
        this.xpNeededForNextLevel = xpForNextLevel - currentXp;
        this.badges = badges;
    }
}
