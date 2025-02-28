package com.company.www.repository.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Asset;
import com.company.www.entity.work.account.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset save(Asset asset);
    Asset findByWork(Work work);
    Asset findByNameAndDateAndDepartment(String name, LocalDate date, Department department);
    List<Asset> findAll();
}
