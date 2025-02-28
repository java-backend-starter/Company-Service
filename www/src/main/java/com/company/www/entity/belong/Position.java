package com.company.www.entity.belong;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="positions")
@Getter
@Setter
public class Position {

    @Id
    @Column(name="position_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @Column(name="position_name")
    private String positionName;

    @Column(name="position_rank")
    private int positionRank;

    @OneToMany(mappedBy="position")
    private List<Staff> staff;

}
