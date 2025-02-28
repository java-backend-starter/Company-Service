package com.company.www.dto.staff;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StaffDto {

    private Long memberId;
    private String userId;
    private String password;
    private String password_confirm;

    private String staffName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String address;
    private String contact;
    private LocalDate employDate;
    private LocalDate resignDate;
    private String checkResign;

    private String authority;
    private String position;
    private String role;
    private String department;
    private String section;

}