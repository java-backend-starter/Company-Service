package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="asset_type")
@Getter
@Setter
public class AssetType {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="asset_type_id")
    private Long assetTypeId;

    @Column(name="super_code")
    private String superCode; // 자산 코드(대분류)

    @Column
    private String type; // 자산 분류 : 건물, 구축물, 비품, 권리 등

    @Column(name="useful_year")
    private int usefulYear; // 내용년수

    @Column(name="depreciation_method")
    private String depreciationMethod; // 상각방법

    @OneToMany(mappedBy="assetType")
    private List<Asset> asset;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
