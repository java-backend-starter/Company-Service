package com.company.www.repository.work.account;

import com.company.www.entity.work.account.TestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {
    TestData save(TestData testData);

    TestData findByTestDataId(Long id);
}
