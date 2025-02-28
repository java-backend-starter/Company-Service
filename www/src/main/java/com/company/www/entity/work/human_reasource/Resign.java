package com.company.www.entity.work.human_reasource;

import com.company.www.constant.staff.ResignState;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="resign")
@Getter
@Setter
public class Resign {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="resign_id")
    private Long resignId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="resign_staff")
    @NotNull(message = "퇴사 직원 정보를 입력해야 합니다.")
    private Staff resignStaff;

    @Column(name="request_date")
    private LocalDate requestDate; // 퇴사 신청 날짜

    @Column(name="process_date")
    private LocalDate processDate; // 퇴사 처리 날짜

    @Column
    private LocalDate resignDate;

    @Column
    @Enumerated(EnumType.STRING)
    private ResignState resignState;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="work_id")
    private Work work;
}
