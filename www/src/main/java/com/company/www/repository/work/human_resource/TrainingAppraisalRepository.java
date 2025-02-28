package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.TrainingAppraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingAppraisalRepository extends JpaRepository<TrainingAppraisal, Long> {
    TrainingAppraisal save(TrainingAppraisal trainingAppraisal);
    TrainingAppraisal findByWork(Work work);
}
