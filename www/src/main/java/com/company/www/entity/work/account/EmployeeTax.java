package com.company.www.entity.work.account;

import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="employee_tax")
@Getter
@Setter
public class EmployeeTax {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="employee_tax_id")
    private Long employeeTaxId;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Staff staff;

    @Column
    private long base;

    @Column
    private long tax;

    @Column
    private LocalDate date;
}
