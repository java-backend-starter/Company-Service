package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="develop_bill")
@Getter
@Setter
public class DevelopBill {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="develop_bill_id")
    private Long develop_bill_id;

    @OneToOne
    @JoinColumn(name="develop_order_id")
    private DevelopOrder developOrder;

    @Column
    private long received;

    @Column
    private long rest;

    @Column(name="receive_statement")
    private String receiveStatement;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
