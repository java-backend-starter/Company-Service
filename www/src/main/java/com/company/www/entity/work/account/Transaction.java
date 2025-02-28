package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="transaction_of_property")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private Long transactionId;

    @Column
    private String item; // 항목
    
    // 차변
    @Column(name="debit_type")
    private String debitType;

    @Column(name="debit_statement")
    private String debitStatement;

    @Column
    private long debit;

    // 대변
    @Column(name="credit_type")
    private String creditType;

    @Column(name="credit_statement")
    private String creditStatement;

    @Column
    private long credit;

    @Column
    private String statement;

    @Column
    private LocalDate date;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
