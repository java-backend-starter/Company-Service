package com.company.www.dto.convenience;

import com.company.www.entity.staff.Staff;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoDto {
    private Long memoId;
    private String title;
    private String content;
    private String memoStatus;
    private String memoType;
    private Staff staff;
}
