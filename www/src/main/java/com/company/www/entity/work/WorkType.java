package com.company.www.entity.work;

import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="work_type")
@Getter
@Setter

public class WorkType {

    @Id
    @Column(name="work_type_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long workTypeId;

    @Column(name="work_name")
    private String workName;

    @Column(name="work_link")
    private String workLink;

    @ManyToOne
    @JoinColumn(name="role", referencedColumnName="role_name")
    private Role role;

    @ManyToOne
    @JoinColumn(name="arbitrary_position", referencedColumnName="position_name")
    private Position arbitraryPosition;

}