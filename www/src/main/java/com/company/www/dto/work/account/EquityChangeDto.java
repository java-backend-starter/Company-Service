package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquityChangeDto {
    private Long equityChangeId;
    private String [] type;
    private long [] capital; // 자본금
    private long [] surplus; // 자본잉여금
    private long [] adjustment; // 자본조정
    private long [] unappropriated; // 미처분이익잉여금
    private long [] comprehensive; // 기타포괄손익누계액
    private long [] total;
}
