package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction save(Transaction transaction);
    Transaction findByWork(Work work);
}
