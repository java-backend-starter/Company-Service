package com.company.www.repository.work.human_resource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.human_reasource.Commute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CommuteRepository extends JpaRepository<Commute, Long> {
    List<Commute> findAllByCommuteDate(LocalDate commuteDate);
    List<Commute> findALlByStaff(Staff staff);
    List<Commute> findAllByCommuteDateAndStaff(LocalDate commuteDate, Staff staff);

    Page<Commute> findAllByCommuteDate(Pageable pageable, LocalDate commuteDate);
    Page<Commute> findAllByStaff(Pageable pageable, Staff staff);
    Page<Commute> findAllByStaffAndCommuteDate(Pageable pageable, Staff staff, LocalDate commuteDate);
    Page<Commute> findAllByStaffAndCommuteDateBetween(Pageable pageable, Staff staff, LocalDate startDate, LocalDate endDate);
}
