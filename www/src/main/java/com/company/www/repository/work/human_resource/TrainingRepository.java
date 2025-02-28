package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Training save(Training training);
    Training findByWork(Work work);
    Training findByName(String name);
}
