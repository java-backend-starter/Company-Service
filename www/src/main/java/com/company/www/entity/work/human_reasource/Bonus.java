package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="bonus")
@Getter
@Setter
public class Bonus {
    @Id
    @Column(name="bonus_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long bonusId;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String reason;

    @Column
    private long amounts; // 보너스를 금액으로 추가할 때

    @Column
    private double ratio; // 보너스를 월급을 기준으로 추가할 때
}
