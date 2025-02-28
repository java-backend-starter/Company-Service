package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="equity_change")
@Getter
@Setter
public class EquityChange {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="equity_change_id")
    private Long equityChangeId;

    @Column
    private String type;

    @Column
    private long capital; // 자본금

    @Column
    private long surplus; // 자본잉여금

    @Column
    private long adjustment; // 자본조정

    @Column
    private long unappropriated; // 미처분이익잉여금

    @Column
    private long comprehensive; // 기타포괄손익누계액

    @Column
    private long total;

    @ManyToOne
    @JoinColumn(name="equity_change_statement")
    private EquityChangeStatement equityChangeStatement;
}
