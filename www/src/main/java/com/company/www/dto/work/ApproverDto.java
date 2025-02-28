package com.company.www.dto.work;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApproverDto {
    private String approvalStaff;
    private String approvalState;
    private LocalDateTime approvalTime;
}
