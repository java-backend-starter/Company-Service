package com.company.www.entity.work.human_reasource;

import com.company.www.constant.work.CommuteType;
import com.company.www.constant.work.PartialLeaveType;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="commute")
@Getter
@Setter
public class Commute {
    @Id
    @Column(name="commute_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long commuteId;

    // 출퇴근 기록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column(name="commute_date")
    private LocalDate commuteDate;

    @Column(name="clock_in")
    private LocalTime clockIn;

    @Column(name="clock_out")
    private LocalTime clockOut;
    
    @Column
    private String reason; // 지각이나 조퇴, 반차 이유 작성
    
    // 일일 결산

    @Column(name="commute_type")
    @Enumerated(EnumType.STRING)
    private CommuteType commuteType; // 출근, 지각, 결근

    @Column(name="partial_leave_type")
    @Enumerated(EnumType.STRING)
    private PartialLeaveType partialLeaveType; // 조퇴, 오전 반차, 오후 반차

    @Column(columnDefinition="int default 0")
    private int overtime; // 연장근무, 퇴근 시간이 일정 시간을 넘기면 기록, 분 단위로 기록

    @Column
    private String statement; // partial_leave_type이 발생하거나 commute_type에서 지각과 결근이 나왔을 때의 사유

    // 주간 결산시 저장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="commute_weekly_record_id", nullable=true)
    private CommuteWeeklyRecord commuteWeeklyRecord;

    // 출퇴근 일일 결산시 작성
    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
