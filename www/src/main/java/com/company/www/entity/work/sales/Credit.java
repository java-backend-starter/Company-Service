package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Debt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="credit")
@Getter
@Setter
public class Credit {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="credit_id")
    private Long creditId;

    @Column
    private String grade; // 신용 등급

    @Column
    private int score; // 신용 점수

    @Column(name="total_payable")
    private long totalPayable; // 채무

    @Column
    private long paid; // 채무 변제액

    @Column(name="total_receivable")
    private long totalReceivable; // 채권

    @Column
    private long received; // 채권 상환액

    @OneToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @OneToMany(mappedBy="credit")
    private List<Debt> debt;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
