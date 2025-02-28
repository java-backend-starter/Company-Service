package com.company.www.entity.work.design;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="resource")
@Getter
@Setter
public class Resource {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="resource_id")
    private Long resourceId;

    @Column
    private String subject;

    @Column
    private LocalDate complete;

    @Column
    private String statement;

    @Column
    private String appraisal;

    // 파일(디자인)

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
