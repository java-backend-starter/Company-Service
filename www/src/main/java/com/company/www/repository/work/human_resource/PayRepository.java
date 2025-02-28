package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Pay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {
    Pay save(Pay pay);
    Pay findByWork(Work work);
}
