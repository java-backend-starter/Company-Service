package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatronRepository extends JpaRepository<Patron, Long> {
    Patron save(Patron patron);
    Patron findByWork(Work work);
    Patron findByNameAndTypeAndLocationAndContact(String name, String Type, String Location, String contact);
}
