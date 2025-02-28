package com.company.www.entity.work.human_reasource;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="training")
@Getter
@Setter
public class Training {
    // 교육 프로그램 내용
    @Id
    @Column(name="training_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long trainingId;

    @Column
    private String name;

    @Column
    private String content;

    @Column
    private String statement;

    @Column(name="training_day")
    private int trainingDay;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @OneToMany
    private List<Trainee> trainees;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
