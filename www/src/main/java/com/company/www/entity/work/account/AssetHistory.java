package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="asset_history")
@Getter
@Setter
public class AssetHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="asset_history_id")
    private Long assetHistoryId;

    @Column
    private LocalDate date; // 감가상각일

    @Column
    private int month; // 상각월수

    @Column(name="former_accumulated_depreciation")
    private long formerAccumulatedDepreciation; // 전기말 감가상각 누계액

    @Column(name="current_depreciation")
    private long currentDepreciation; // 당기 감가상각액

    @Column(name="current_accumulated_depreciation")
    private long currentAccumulatedDepreciation; // 당기말 감가상각 누계액

    @Column(name="current_residual_value")
    private long currentResidualValue; // 당기 잔존가액

    @ManyToOne
    @JoinColumn(name="asset_id")
    private Asset asset;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
