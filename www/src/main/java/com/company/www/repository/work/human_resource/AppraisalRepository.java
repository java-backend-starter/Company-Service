package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Appraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    Appraisal save(Appraisal appraisal);
    Appraisal findByWork(Work work);
}
