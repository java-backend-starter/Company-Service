package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.SoftwareSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SoftwareSalesRepository extends JpaRepository<SoftwareSales, Long> {
    SoftwareSales save(SoftwareSales softwareSales);
    SoftwareSales findByWork(Work work);
    List<SoftwareSales> findAllBySalesDateBetween(LocalDate startDate, LocalDate endDate);
}
