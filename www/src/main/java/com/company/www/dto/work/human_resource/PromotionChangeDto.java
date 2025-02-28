package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PromotionChangeDto {
    private String staff;
    private String userId;
    private String currentPosition;
    private String currentRole;
    private String nextPosition;
    private String nextRole;
    private String reason;
    private LocalDate date;
}
