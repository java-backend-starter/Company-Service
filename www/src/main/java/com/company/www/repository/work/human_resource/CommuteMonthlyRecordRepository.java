package com.company.www.repository.work.human_resource;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.CommuteMonthlyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommuteMonthlyRecordRepository extends JpaRepository<CommuteMonthlyRecord, Long> {
    Page<CommuteMonthlyRecord> findAllByRecordMonth(Pageable pageable, String recordMonth);
    CommuteMonthlyRecord findByWork(Work work);
}
