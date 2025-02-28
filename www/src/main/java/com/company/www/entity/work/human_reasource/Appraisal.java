package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="appraisal")
@Getter
@Setter
public class Appraisal {
    // 인사고과 자료
    @Id
    @Column(name="appraisal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appraisalId;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column
    private LocalDate date;

    // 기여도, 판단력, 업무개선, 추진력, 문제해결
    // 정확성, 조직관리, 성실성, 공감능력, 애사심
    // Excellent : 최우수, Very good : 우수, good : 보통
    // needed improvement : 미흡, unsatisfactory : 낙제
    // not relevant : 평가 제외 항목

    @Column
    private String contribution;

    @Column
    private String judge;

    @Column
    private String improvement;

    @Column
    private String propulsion;

    @Column
    private String resolution;

    @Column
    private String accuracy;

    @Column
    private String management;

    @Column
    private String sincerity;

    @Column
    private String sympathy;

    @Column
    private String belonging;

    @Column
    private String result;

    @Column
    private String statement;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
