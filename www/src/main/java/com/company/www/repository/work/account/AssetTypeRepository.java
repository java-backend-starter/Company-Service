package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, Long> {
    AssetType save(AssetType assetType);
    AssetType findByWork(Work work);
    AssetType findBySuperCode(String superCode);
}
