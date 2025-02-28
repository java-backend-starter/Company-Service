package com.company.www.entity.work.human_reasource;

import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="pay_info")
@Getter
@Setter
public class PayInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="pay_info_id")
    private Long payInfoId;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="position_id")
    private Position position;

    @Column
    private long base; // 기본급 == 한달 단위의 StandardCost.base

    @Column(name="position_bonus")
    private long positionBonus;

    @Column
    private long night; // 야간근무수당

    @Column
    private long overtime; // 연장근무수당

    @Column
    private long weekend; // 주말근무수당
    
    @Column
    private long holiday; // 주휴수당

    @Column
    private int year;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
