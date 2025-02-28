package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.Patron;
import com.company.www.entity.work.sales.SoftwareQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareQuotationRepository extends JpaRepository<SoftwareQuotation, Long> {
    SoftwareQuotation save(SoftwareQuotation softwareQuotation);
    SoftwareQuotation findByWork(Work work);
    SoftwareQuotation findByQuotationAndPatron(String quotation, Patron patron);
}
