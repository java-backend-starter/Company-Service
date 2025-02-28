package com.company.www.repository.work.design;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.design.Design;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {
    Design save(Design design);
    Design findByWork(Work work);
}
