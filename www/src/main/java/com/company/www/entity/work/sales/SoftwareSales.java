package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.information_technology.Software;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="software_sales")
@Getter
@Setter
@NoArgsConstructor
public class SoftwareSales {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="software_sales_id")
    private Long softwareSalesId;

    @ManyToOne
    @JoinColumn(name="software_id")
    private Software software;

    @Column(name="sales_date")
    private LocalDate salesDate;

    @Column
    private int quantity;

    @Column
    private long total;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
