package com.company.www.entity.work;

import com.company.www.constant.work.ApprovalPosition;
import com.company.www.converter.EnumListConverter;
import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="approval_line")
@Getter
@Setter
public class ApprovalLine {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="approval_line_id")
    private Long approvalLineId;

    @Column(name="approver_size")
    private int approverSize;

    @ManyToOne
    @JoinColumn(name="role", referencedColumnName="role_name")
    private Role role;

    @Column
    @Convert(converter= EnumListConverter.class)
    private List<ApprovalPosition> approvalPositions;
}
