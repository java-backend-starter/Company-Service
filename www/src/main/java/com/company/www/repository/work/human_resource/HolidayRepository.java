package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    Holiday save(Holiday holiday);
    Holiday findByWork(Work work);

}
