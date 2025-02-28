package com.company.www.entity.work;

import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.entity.belong.Position;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="work")
@Getter
@Setter

public class Work {
    @Id
    @Column(name="work_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long workId;

    @ManyToOne
    @JoinColumn(name="work_type_id")
    private WorkType workType;

    // 문서 정보

    @Column
    private String subject;

    @Column(name="retention_period")
    private String retentionPeriod;

    @Column(name="security_level")
    private String securityLevel;

    // 결재 관련자

    @ManyToOne
    @JoinColumn(name="draft_staff_id")
    private Staff draftStaff;

    @Column(name="draft_state")
    @Enumerated(EnumType.STRING)
    private WorkState draftState;

    @Column(name="draft_date")
    private LocalDateTime draftDate;

    @OneToMany
    private List<Approver> approvers;

    @OneToOne
    @JoinColumn(name="approval_line")
    private ApprovalLine approvalLine;

}