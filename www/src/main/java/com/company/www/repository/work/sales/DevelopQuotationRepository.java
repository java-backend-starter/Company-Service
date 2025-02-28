package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.DevelopQuotation;
import com.company.www.entity.work.sales.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevelopQuotationRepository extends JpaRepository<DevelopQuotation, Long> {
    DevelopQuotation save(DevelopQuotation developQuotation);
    DevelopQuotation findByWork(Work work);
    DevelopQuotation findByQuotationAndPatron(String quotation, Patron patron);
}
