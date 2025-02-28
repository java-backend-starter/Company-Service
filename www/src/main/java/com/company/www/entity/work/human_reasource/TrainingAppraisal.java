package com.company.www.entity.work.human_reasource;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="training_appraisal")
@Getter
@Setter
public class TrainingAppraisal {
    // 교육대상자 평가
    @Id
    @Column(name="training_appraisal_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long trainingAppraisalId;

    @ManyToOne
    @JoinColumn(name="training_id")
    private Training training;

    @ManyToOne
    @JoinColumn(name="trainee_id")
    private Trainee trainee;

    @Column
    private String grade;

    @Column
    private String appraisal;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
