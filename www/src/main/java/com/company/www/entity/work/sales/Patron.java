package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="patron")
@Getter
@Setter
public class Patron {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="patron_id")
    private Long patronId;

    @Column
    private String name;

    @Column
    private String type; // 업종

    @Column
    private String location;

    @Column
    private String contact;

    @Column
    private String statement;

    @OneToOne
    @JoinColumn(name="credit_id")
    private Credit credit;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
