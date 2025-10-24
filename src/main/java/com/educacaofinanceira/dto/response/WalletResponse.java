package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Wallet;
import lombok.Data;

import java.util.UUID;

@Data
public class WalletResponse {

    private UUID id;
    private UUID childId;
    private String childName;
    private Integer balance;
    private Integer totalEarned;
    private Integer totalSpent;

    public static WalletResponse fromWallet(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setChildId(wallet.getChild().getId());
        response.setChildName(wallet.getChild().getFullName());
        response.setBalance(wallet.getBalance());
        response.setTotalEarned(wallet.getTotalEarned());
        response.setTotalSpent(wallet.getTotalSpent());
        return response;
    }
}
