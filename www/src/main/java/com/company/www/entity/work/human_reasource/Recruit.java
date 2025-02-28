package com.company.www.entity.work.human_reasource;

import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="recruit")
@Getter
@Setter
public class Recruit {

    @Id
    @Column(name="recruit_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long recruitId;

    @Column(name="recruit_name")
    private String recruitName; // 채용공고 이름

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role; // 직무

    @ManyToOne
    @JoinColumn(name="position_id")
    private Position position; // 직위

    @Column
    private String qualification; // 자격요건

    @Column
    private String preference; // 우대요건

    @Column(name="recruit_number")
    private int recruitNumber; // 채용인원

    @Column(name="employment_status")
    private String employmentStatus; // 고용상태

    @Column
    private long pay; // 연봉

    @Column
    private String place; // 근무지

    @Column
    private String process; // 채용절차

    @Column
    private String document; // 지원서류

    @Column
    private String means; // 지원방법

    @Column(name="start_date")
    private LocalDate startDate; // 지원 시작일

    @Column(name="end_date")
    private LocalDate endDate; // 지원 종료일

    @OneToMany(mappedBy="recruit")
    private List<Applicant> applicants;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
