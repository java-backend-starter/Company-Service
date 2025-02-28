package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="software_quotation")
@Getter
@Setter
public class SoftwareQuotation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="software_quotation_id")
    private Long softwareQuotationId;

    @ManyToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @Column
    private String quotation;

    @Column
    private LocalDate date;

    @OneToMany(mappedBy="softwareQuotation")
    private List<SoftwareItem> softwareItems;

    @Column(name="total_price")
    private long totalPrice;

    @OneToOne
    @JoinColumn(name="software_order_id")
    private SoftwareOrder softwareOrder;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
