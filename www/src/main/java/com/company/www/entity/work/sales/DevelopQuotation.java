package com.company.www.entity.work.sales;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="develop_quotation")
@Getter
@Setter
public class DevelopQuotation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="develop_quotation_id")
    private Long developQuotationId;

    @Column
    private String quotation;

    @Column
    private LocalDate date;

    @Column
    private long developCost;

    @Column
    private String option;

    @Column
    private String statement;

    @ManyToOne
    @JoinColumn(name="patron_id")
    private Patron patron;

    @OneToMany
    private List<Unit> units;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
