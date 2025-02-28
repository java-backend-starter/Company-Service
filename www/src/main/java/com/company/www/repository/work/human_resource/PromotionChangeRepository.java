package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.PromotionChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionChangeRepository extends JpaRepository<PromotionChange, Long> {
    PromotionChange save(PromotionChange promotionChange);
    PromotionChange findByWork(Work work);
}
