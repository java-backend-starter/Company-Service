package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="debt_history")
@Getter
@Setter
public class DebtHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="debt_history_id")
    private Long debtHistoryId;

    @Column
    private LocalDate date;

    @Column
    private long amount;

    @Column
    private String status;

    @Column
    private String statement;

    @ManyToOne
    @JoinColumn(name="debt_id")
    private Debt debt;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
