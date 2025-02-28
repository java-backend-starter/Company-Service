package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BalanceSheetDto {

    private String name;
    private LocalDate date;
    // 유동자산
    // 당좌자산
    private long cash; // 현
    private long securities; // 유가증권
    private long receivable; // 외상매출금
    private long receivableAccount; // 미수금
    private long otherLiquidAsset; // 기타 유동자산

    // 비유동자산

    // 투자자산
    private long longTermDeposit; // 장기성예금
    private long stock; // 투자주식
    private long property; // 투자부동산

    // 유형자산
    private long ground; // 토지
    private long building; // 건물
    private long machine; // 기계장치
    private long equipment; // 비품

    // 무형자산
    private long product; // 제품
    private long otherFixedAsset; // 기타 비유동자산

    // 부채

    // 유동부채
    private long shortTermBorrowing; // 단기차입금
    private long payable; // 외상매입금
    private long payableAccount; // 미지급금
    private long advanceReceived; // 선수금
    private long otherLiquidLiability; // 기타 유동부채

    // 비유동부채
    private long privateLoan; // 사채
    private long longTermBorrowing; // 장기차입금
    private long severancePay; // 퇴직금급여충당부채
    private long otherFixedLiability; // 기타 비유동부채

    // 자본
    private long capital; // 자본금
    private long surplus; // 자본잉여금
    private long adjustment; // 자본조정
    private long unappropriated; // 미처분이익잉여금
    private long comprehensive; // 기타포괄손익누계액
}
