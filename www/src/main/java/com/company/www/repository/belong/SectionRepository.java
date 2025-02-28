package com.company.www.repository.belong;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    Section save(Section section);
    Section findBySectionName(String sectionName);
}
