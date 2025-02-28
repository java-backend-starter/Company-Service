package com.company.www.entity.work.information_technology;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="software")
@Getter
@Setter
public class Software {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="software_id")
    private Long softwareId;

    @Column(name="software_name")
    private String softwareName;

    @Column(name="software_type")
    private String softwareType;
    
    @Column(name="develop_date")
    private LocalDate developDate; // 개발일자

    @Column
    private long price;

    @Column
    private String statement;

    @Column(name="main_function")
    private String mainFunction;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    // 소프트웨어 파일(압축 파일)
}
