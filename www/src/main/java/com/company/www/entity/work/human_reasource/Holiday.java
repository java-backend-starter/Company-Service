package com.company.www.entity.work.human_reasource;

import com.company.www.constant.work.HolidayType;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="holiday")
@Getter
@Setter

public class Holiday {

    @Id
    @Column(name="holiday_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holidayId;

    // 휴가 관련 정보
    @Column(name="holiday_type")
    private HolidayType holidayType; // 휴가 종류

    @Column(name="start_date")
    private LocalDate startDate; // 휴가 시작

    @Column(name="end_date")
    private LocalDate endDate; // 휴가 종료

    @Column
    private String half; // 반차 여부

    @Column
    private String reason; // 휴가 사유

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
