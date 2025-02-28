package com.company.www.repository.work.design;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.design.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Resource save(Resource resource);
    Resource findByWork(Work work);
}
