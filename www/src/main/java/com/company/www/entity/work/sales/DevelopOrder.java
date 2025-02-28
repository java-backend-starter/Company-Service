package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="develop_order")
@Getter
@Setter
public class DevelopOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="develop_order_id")
    private Long developOrderId;

    @OneToOne
    @JoinColumn(name="develop_quotation_id")
    private DevelopQuotation developQuotation;

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

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
