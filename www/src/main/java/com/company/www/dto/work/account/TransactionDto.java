package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionDto {
    private String item;
    private String debitType;
    private String debitStatement;
    private long debit;
    private String creditType;
    private String creditStatement;
    private long credit;
    private String statement;
    private LocalDate date;
}
