package com.company.www.entity.work.sales;

import com.company.www.entity.work.information_technology.Software;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="software_list")
@Getter
@Setter
public class SoftwareItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="software_item_id")
    private Long softwareItemId;

    @ManyToOne
    @JoinColumn(name="software_id")
    private Software software;

    @Column
    private int quantity;

    @Column(name="software_total_price")
    private long softwareTotalPrice;

    @ManyToOne
    @JoinColumn(name="software_quotation_id")
    private SoftwareQuotation softwareQuotation;

}
