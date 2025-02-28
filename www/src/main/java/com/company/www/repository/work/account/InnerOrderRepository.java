package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.InnerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InnerOrderRepository extends JpaRepository<InnerOrder, Long> {
    InnerOrder save(InnerOrder innerOrder);
    InnerOrder findByWork(Work work);
}
