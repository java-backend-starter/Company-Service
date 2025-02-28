package com.company.www.repository.work.account;

import com.company.www.entity.work.account.AdditionalBudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalBudgetItemRepository extends JpaRepository<AdditionalBudgetItem, Long> {
    AdditionalBudgetItem save(AdditionalBudgetItem additionalBudgetItem);
    void delete(AdditionalBudgetItem additionalBudgetItem);
    AdditionalBudgetItem findByAdditionalBudgetItemId(Long additionalBudgetItemId);
}
