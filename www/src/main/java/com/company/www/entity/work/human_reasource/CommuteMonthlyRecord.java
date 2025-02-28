package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="commute_monthly_record")
@Getter
@Setter
public class CommuteMonthlyRecord {
    @Id
    @Column(name="commute_monthly_record_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long commuteMonthlyRecordId;

    // 근태 결산 관련 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column(name="record_date")
    private LocalDate recordDate; // 근태 결산일시

    @Column(name="record_month")
    private String recordMonth; // 결산 기준일

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
    private int overtime; // 연장근무 시간(기준 : 시간)

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

    @OneToMany(fetch=FetchType.LAZY, mappedBy="commuteMonthlyRecord")
    private List<CommuteWeeklyRecord> commuteWeeklyRecords;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
