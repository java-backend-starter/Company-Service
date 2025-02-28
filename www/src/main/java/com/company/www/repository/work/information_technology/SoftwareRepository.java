package com.company.www.repository.work.information_technology;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.information_technology.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SoftwareRepository extends JpaRepository<Software, Long> {
    Software save(Software software);
    Software findByWork(Work work);
    Software findBySoftwareNameAndSoftwareType(String softwareName, String SoftwareType);
    Software findBySoftwareNameAndSoftwareTypeAndDevelopDateAndPrice(String softwareName, String SoftwareType, LocalDate developDate, long price);
}
