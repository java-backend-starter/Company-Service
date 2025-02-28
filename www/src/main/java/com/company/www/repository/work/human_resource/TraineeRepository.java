package com.company.www.repository.work.human_resource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Trainee save(Trainee trainee);
    Trainee findByWork(Work work);
    Trainee findByStaff(Staff staff);
}
