package com.company.www.dto.work;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkDto {
    private Long workId;

    private String subject;
    private String retentionPeriod;
    private String securityLevel;

    private String draftStaff;
    private String draftState;
    private LocalDateTime draftDate;
}
