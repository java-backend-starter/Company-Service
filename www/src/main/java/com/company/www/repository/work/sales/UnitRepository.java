package com.company.www.repository.work.sales;

import com.company.www.entity.work.account.StandardCost;
import com.company.www.entity.work.sales.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit save(Unit unit);
    Unit findByUnitId(Long unitId);
    Unit findByStandardCostAndDevelopQuotation(StandardCost standardCost, String developQuotation);
    void delete(Unit unit);
}
