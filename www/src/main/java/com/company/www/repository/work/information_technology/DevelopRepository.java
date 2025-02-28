package com.company.www.repository.work.information_technology;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.information_technology.Develop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevelopRepository extends JpaRepository<Develop, Long> {
    Develop save(Develop develop);
    Develop findByWork(Work work);
}
