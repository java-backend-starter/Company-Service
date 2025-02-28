package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="commute_weekly_record")
@Getter
@Setter
public class CommuteWeeklyRecord {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="commute_weekly_record_id")
    private Long commuteWeeklyRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column(name="record_date")
    private LocalDate recordDate; // 근태 결산일시

    @Column(name="record_week")
    private String recordWeek; // 결산 기준일

    @Column
    @Min(0)
    @Max(31)
    private int attendance; // 출근

    @Column(name="work_time")
    @Min(0)
    @Max(744)
    private int workTime; // 근무시간(기준 : 시간)

    @Column
    @Min(0)
    @Max(744)
    private int overtime; // 연장근무 시간(기준 : 시간, commute에서 overtime을 60으로 나눠서 저장)

    @Column
    @Min(0)
    @Max(31)
    private int absence; // 결석

    @Column
    @Min(0)
    @Max(31)
    private int lateness; // 지각

    @Column
    @Min(0)
    @Max(31)
    private double holiday; // 휴가

    @OneToMany(fetch= FetchType.LAZY, mappedBy="commuteWeeklyRecord")
    private List<Commute> commutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="commute_monthly_record_id", nullable=true)
    private CommuteMonthlyRecord commuteMonthlyRecord;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
