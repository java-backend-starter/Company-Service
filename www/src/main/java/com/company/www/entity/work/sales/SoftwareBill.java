package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="software_bill")
@Getter
@Setter
public class SoftwareBill {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="software_bill_id")
    private Long software_bill_id;

    @OneToOne
    @JoinColumn(name="software_order_id")
    private SoftwareOrder softwareOrder;

    @Column
    private long received;

    @Column
    private long rest;

    @Column(name="receive_statement")
    private String receiveStatement;

    @ManyToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
