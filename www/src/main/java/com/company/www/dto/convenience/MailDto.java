package com.company.www.dto.convenience;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDto {
    private String title;
    private String content;
    private String sendStaff;
    private String receiveStaff;
    private String mailType;
    private boolean isForMe;
    // 파일 입출력 관련 추가 예정
}
