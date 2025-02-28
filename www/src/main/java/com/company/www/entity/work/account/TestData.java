package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="test_data")
@Getter
@Setter
public class TestData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="test_data_id")
    private Long testDataId;

    @Column
    private String data;
}
