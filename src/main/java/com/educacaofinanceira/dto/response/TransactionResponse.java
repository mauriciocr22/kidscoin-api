package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Transaction;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID id;
    private TransactionType type;
    private Integer amount;
    private Integer balanceBefore;
    private Integer balanceAfter;
    private String description;
    private ReferenceType referenceType;
    private UUID referenceId;
    private LocalDateTime createdAt;

    public static TransactionResponse fromTransaction(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceBefore(transaction.getBalanceBefore());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setReferenceType(transaction.getReferenceType());
        response.setReferenceId(transaction.getReferenceId());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
