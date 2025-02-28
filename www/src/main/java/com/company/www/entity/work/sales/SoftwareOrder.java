package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="software_order")
@Getter
@Setter
public class SoftwareOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="software_order_id")
    private Long softwareOrderId;

    @OneToOne
    @JoinColumn(name="software_quotation_id")
    private SoftwareQuotation softwareQuotation;

    @Column(name="order_code")
    private String orderCode;

    @Column
    private String status;

    @Column
    private String payment;

    @Column(name="order_date")
    private LocalDate orderDate;

    @Column(name="provide_date")
    private LocalDate provideDate;

    @Column
    private String statement;

    @ManyToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
