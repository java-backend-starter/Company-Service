package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="trainee")
@Getter
@Setter
public class Trainee {
    //교육 대상자 + 피드백
    @Id
    @Column(name="trainee_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long traineeId;

    @OneToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="training_id")
    private Training training;

    @OneToMany(mappedBy="trainee")
    private List<TrainingAppraisal> trainingAppraisals;

    @Column
    private String feedback;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
