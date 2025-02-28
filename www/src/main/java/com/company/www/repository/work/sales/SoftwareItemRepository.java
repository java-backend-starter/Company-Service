package com.company.www.repository.work.sales;

import com.company.www.entity.work.sales.SoftwareItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareItemRepository extends JpaRepository<SoftwareItem, Long> {
    SoftwareItem save(SoftwareItem softwareItem);
    SoftwareItem findBySoftwareItemId(Long softwareItemId);
    void delete(SoftwareItem softwareItem);
}
