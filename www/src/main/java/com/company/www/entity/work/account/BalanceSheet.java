package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="balance_sheet")
@Getter
@Setter
public class BalanceSheet {
    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long balanceSheetId;

    @Column
    private String name;

    @Column
    private LocalDate date;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    // 유동자산
    // 당좌자산
    @Column
    private long cash; // 현금

    @Column
    private long securities; // 유가증권

    @Column
    private long receivable; // 외상매출금

    @Column(name="receivable_account")
    private long receivableAccount; // 미수금

    @Column(name="other_liquid_asset")
    private long otherLiquidAsset; // 기타 유동자산
    
    // 비유동자산
    // 투자자산
    @Column(name="long_term_deposit")
    private long longTermDeposit; // 장기성예금

    @Column
    private long stock; // 투자주식

    @Column
    private long property; // 투자부동산

    // 유형자산
    @Column
    private long ground; // 토지

    @Column
    private long building; // 건물

    @Column
    private long machine; // 기계장치

    @Column
    private long equipment; // 비품

    // 무형자산
    @Column
    private long product; // 제품

    @Column(name="other_fixed_asset")
    private long otherFixedAsset; // 기타 비유동자산

    // 부채
    // 유동부채
    @Column(name="short_term_borrowing")
    private long shortTermBorrowing; // 단기차입금

    @Column
    private long payable; // 외상매입금

    @Column(name="payable_account")
    private long payableAccount; // 미지급금

    @Column(name="advance_received")
    private long advanceReceived; // 선수금

    @Column(name="other_liquid_liability")
    private long otherLiquidLiability; // 기타 유동부채
    
    // 비유동부채
    @Column(name="private_loan")
    private long privateLoan; // 사채
    
    @Column(name="long_term_borrowing")
    private long longTermBorrowing; // 장기차입금
    
    @Column(name="severance_pay")
    private long severancePay; // 퇴직금급여충당부채

    @Column(name="other_fixed_liability")
    private long otherFixedLiability; // 기타 비유동부채
    
    // 자본
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

    public long getGrossLiquidAsset(){
        return (cash + securities + receivable + receivableAccount + otherLiquidAsset);
    }
    public long getGrossFixedAsset(){
        return (longTermDeposit + stock + property + ground + building + machine + equipment + product + otherFixedAsset);
    }
    public long getGrossAsset(){
        return (getGrossLiquidAsset() + getGrossFixedAsset()); // == getGrossProperty()
    }

    public long getGrossLiquidLiability(){
        return (shortTermBorrowing + payable + payableAccount +  advanceReceived + otherLiquidLiability);
    }
    public long getGrossFixedLiability(){
        return (privateLoan + longTermBorrowing + severancePay + otherFixedLiability);
    }
    public long getGrossLiability(){
        return (getGrossLiquidLiability() + getGrossFixedLiability());
    }
    public long getGrossCapital(){
        return (capital + surplus + adjustment + unappropriated + comprehensive);
    }
    public long getGrossProperty(){
        return (getGrossLiability() + getGrossCapital()); // == getGrossAsset()
    }
}
