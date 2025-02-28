package com.company.www.entity.staff;

import com.company.www.constant.staff.Gender;
import com.company.www.entity.belong.*;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.entity.work.human_reasource.Resign;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="staff")
@Getter
@Setter

public class Staff {

    @Id
    @Column(name="staff_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    @Column(nullable=false, length=50, unique=true, name="user_id")
    @NotEmpty(message="아이디는 필수 입력입니다.")
    @Length(min=5, max=50, message="아이디는 5자 이상 50자 이하로 입력해주세요.")
    private String userId;

    @Column(nullable=false, length=100)
    @NotEmpty(message = "비밀번호는 필수 입력입니다.")
    @Size(min=8, message="비밀번호는 8자 이상 입력해주세요.")
    private String password;

    // 기본 정보

    @Column(nullable=false, length=30, name="staff_name")
    @NotEmpty(message = "이름은 필수 입력입니다.")
    @Size(min=2, max=50, message="이름을 2자 이상 입력해주세요.")
    private String staffName;

    @Column(nullable=false)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String address;

    @Column
    private String contact;

    @Column(nullable=false, length=80)
    @NotEmpty(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @Column(name="employ_date")
    private LocalDate employDate;

    @OneToOne
    @JoinColumn(name="resign")
    private Resign resign;

    // 소속정보

    @ManyToOne
    @JoinColumn(name="position_name", referencedColumnName="position_name")
    private Position position;

    @ManyToOne
    @JoinColumn(name="role_name", referencedColumnName="role_name")
    private Role role;

    @ManyToOne
    @JoinColumn(name="department_name", referencedColumnName="department_name")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_name", referencedColumnName="section_name")
    private Section section;

}