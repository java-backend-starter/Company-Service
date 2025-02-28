package com.company.www.repository.work;

import com.company.www.entity.belong.Role;
import com.company.www.entity.work.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {
    List<WorkType> findAll();

    List<WorkType> findAllByRole(Role role);

    List<WorkType> findAllByWorkNameEndingWith(String workName);

    WorkType findByWorkName(String workName);

    WorkType findByRole(Role role);

    WorkType save(WorkType workType);

    void delete(WorkType workType);
}