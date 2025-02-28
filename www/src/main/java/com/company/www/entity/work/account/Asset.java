package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="asset")
@Getter
@Setter
public class Asset {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="asset_id")
    private Long assetId;

    @Column(name="sub_code")
    private String subCode; // 자산 코드(소분류)

    @Column
    private String name; // 자산 이름

    @Column
    private LocalDate date; // 취득 일자

    @Column
    private int quantity; // 취득 수량

    @Column
    private long price; // 취득 가격

    @Column
    private String acquisitionMethod; // 취득 방법

    @Column(name="residual_value")
    private long residualValue; // 잔존가액

    @Column(name="depreciation")
    private double depreciation; // 상각비

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department; // 사용 부서

    @Column
    private String status; // 자산 상태

    @ManyToOne
    @JoinColumn(name="asset_type_id")
    private AssetType assetType;

    @OneToMany(mappedBy="asset")
    private List<AssetHistory> assetHistory;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
