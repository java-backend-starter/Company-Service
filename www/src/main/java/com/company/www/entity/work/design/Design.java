package com.company.www.entity.work.design;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="design")
@Getter
@Setter
public class Design {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="design_id")
    private Long DesignId;

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
    private String statement; // 요약

    @Column
    private String appraisal; // 평가

    // 첨부 파일(예시 : 기획서, UI 디자인초안, Wireframe, Storyboard)

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
