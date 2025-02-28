package com.company.www.entity.belong;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="section")
@Getter
@Setter
public class Section {

    @Id
    @Column(name="section_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @Column(name="section_name")
    private String sectionName;

    @ManyToOne
    @JoinColumn(name="department", referencedColumnName="department_name")
    private Department department;

    @OneToMany(mappedBy="section")
    private List<Staff> staff;

}
