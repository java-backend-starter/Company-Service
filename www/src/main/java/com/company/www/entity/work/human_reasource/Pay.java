package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="pay")
@Getter
@Setter
public class Pay {

    @Id
    @Column(name="pay_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long payId;

    @Column
    private int night; // 야간 근무 시간

    @Column
    private int overtime; // 연장 근무 시간

    @Column
    private int weekend; // 주말 근무 시간

    @Column
    private int holiday; // 주휴수당 개수

    @Column(name="total_additional")
    private long totalAdditional;

    @Column(name="additional_night")
    private long additionalNight;

    @Column(name="additional_overtime")
    private long additionalOvertime;

    @Column(name="additional_weekend")
    private long additionalWeekend;

    @Column(name="additional_holiday")
    private long additionalHoliday;

    @Column(name="pay_date")
    private LocalDate payDate;

    @Column
    private String period;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="pay_info_id")
    private PayInfo payInfo;

    @OneToOne
    @JoinColumn(name="payroll_id")
    private Payroll payroll;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
