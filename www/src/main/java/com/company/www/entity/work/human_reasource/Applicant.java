package com.company.www.entity.work.human_reasource;

import com.company.www.constant.staff.Gender;
import com.company.www.entity.belong.Role;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Applicant {

    @Id
    @Column(name="applicant_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long applicantId;

    @Column
    private String name;

    @Column
    private String birthday;

    @Column
    private Gender gender;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @Column
    private String specification;

    @Column
    private int career;

    @Column
    private String qualify;

    @ManyToOne
    @JoinColumn(name="recruit_id")
    private Recruit recruit;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
