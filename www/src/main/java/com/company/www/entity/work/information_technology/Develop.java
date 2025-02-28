package com.company.www.entity.work.information_technology;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="develop")
@Getter
@Setter
public class Develop {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="develop_id")
    private Long developId;

    @Column
    private String project;

    @Column
    private String phase;

    @Column
    private String subject;

    @Column
    private String status;

    @Column
    private LocalDate complete;

    @Column
    private LocalDate expire;

    @Column
    private String statement;

    @Column
    private String appraisal;

    // 파일
    // 종류 : 정리 예정

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
