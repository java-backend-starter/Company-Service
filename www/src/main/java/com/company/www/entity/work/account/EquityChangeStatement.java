package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="equity_change_statement")
@Getter
@Setter
public class EquityChangeStatement {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="equity_change_statement_id")
    private Long equityChangeStatementId;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    @OneToMany(mappedBy="equityChangeStatement")
    private List<EquityChange> equityChange;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;


}
