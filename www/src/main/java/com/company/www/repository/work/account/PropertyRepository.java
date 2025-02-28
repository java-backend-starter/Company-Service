package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Property save(Property property);
    Property findByWork(Work work);
    List<Property> findAllByClosingStartAndClosingEndAndType(LocalDate closingStart, LocalDate closingEnd, String type);
}
