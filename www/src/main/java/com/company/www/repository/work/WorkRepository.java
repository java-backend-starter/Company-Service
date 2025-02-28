package com.company.www.repository.work;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    Work save(Work work);
    Work findByWorkId(Long id);

    Page<Work> findAllByDraftStaffAndWorkTypeOrderByWorkIdDesc(Pageable pageable, Staff draftStaff, WorkType workType);

    Page<Work> findAllByWorkTypeOrderByWorkIdDesc(Pageable pageable, WorkType workType);

    // Page<Work> findAllByWorkTypeAndStateOrderByWorkIdDesc(Pageable pageable, WorkType workType, State state);
}
