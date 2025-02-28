package com.company.www.entity.work.human_reasource;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.account.CostCenter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="performance")
@Getter
@Setter
public class Performance {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="performance_id")
    private Long performanceId;

    @ManyToOne
    @JoinColumn(name="work_type_id")
    private WorkType workType;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @Column
    private String name;

    @Column
    private String plan;

    @Column
    private String archive;

    @Column
    private int ratio;

    @Column(name="performance_statement")
    private String performanceStatement;

    @Column(name="check_staff_statement")
    private String checkStaffStatement;

    @ManyToOne
    @JoinColumn(name="cost_center_id")
    private CostCenter costCenter;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
