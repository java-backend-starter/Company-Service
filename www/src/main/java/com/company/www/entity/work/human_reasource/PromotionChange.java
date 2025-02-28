package com.company.www.entity.work.human_reasource;

import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="promotion_change")
@Getter
@Setter
public class PromotionChange {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="promotion_change_id")
    private Long promotionChangeId;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @Column
    private String currentPosition;

    @Column
    private String currentRole;

    @Column
    private String nextPosition;

    @Column
    private String nextRole;

    @Column
    private String reason;

    @Column
    private LocalDate date;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
