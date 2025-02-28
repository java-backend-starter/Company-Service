package com.company.www.entity.belong;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="role")
@Getter
@Setter
public class Role {

    @Id
    @Column(name="role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name="role_name")
    private String roleName;

    @OneToMany(mappedBy="role")
    private List<Staff> staff;

}
