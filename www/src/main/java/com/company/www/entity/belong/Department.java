package com.company.www.entity.belong;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="department")
@Getter
@Setter
public class Department {

    @Id
    @Column(name="department_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(name="department_name")
    private String departmentName;

    @Column(name="department_english_name")
    private String departmentEnglishName;

    @OneToMany(mappedBy="department")
    private List<Section> sections;

    @OneToMany(mappedBy="department")
    private List<Staff> staff;

}
