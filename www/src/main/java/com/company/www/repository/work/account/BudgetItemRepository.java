package com.company.www.repository.work.account;

import com.company.www.entity.work.account.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {
    BudgetItem save(BudgetItem budgetItem);
    void delete(BudgetItem budgetItem);
    BudgetItem findByBudgetItemId(Long budgetItemId);
}
