package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="payroll")
@Getter
@Setter
public class Payroll {

    @Id
    @Column(name="payroll_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long payrollId;

    @Column(name="national_pension")
    private long nationalPension; // 국민연금

    @Column(name="national_health_insurance")
    private long nationalHealthInsurance; // 국민건강보험

    @Column(name="employment_insurance")
    private long employmentInsurance; // 고용보험

    @Column(name="corporate_employment_insurance")
    private long  corporateEmploymentInsurance; // 고용보험(기업용, 개인 + 0.2% 가산)

    @Column(name="long_term_care_insurance")
    private long longTermCareInsurance; // 노인장기요양보험

    @Column(name="industrial_accident_compensation_insurance")
    private long industrialAccidentCompensationInsurance; // 산업재해보상보험, 인사부만 확인
    
    @Column(name="income_tax")
    private long incomeTax; // 소득세

    @Column(name="local_income_tax")
    private long localIncomeTax; // 지방세

    /* 연말정산, 제외
    @Column(name="year_end_tax_adjustment")
    private long yearEndTaxAdjustment;
     */

    @Column
    private LocalDate date; // 지급일

    @ManyToOne
    @JoinColumn(name="check_staff_id")
    private Staff checkStaff;


    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @OneToOne
    @JoinColumn(name="pay_id")
    private Pay pay;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
