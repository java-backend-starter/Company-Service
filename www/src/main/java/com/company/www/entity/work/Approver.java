package com.company.www.entity.work;

import com.company.www.constant.work.ApprovalState;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="approver")
@Getter
@Setter
public class Approver {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="approver_id")
    private Long approverId;

    @ManyToOne
    @JoinColumn(name="approval_staff")
    private Staff approvalStaff;

    @Column(name="approval_state")
    @Enumerated(EnumType.STRING)
    private ApprovalState approvalState;

    @Column(name="approval_time")
    private LocalDateTime approvalTime;
}
