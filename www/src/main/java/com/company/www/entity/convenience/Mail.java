package com.company.www.entity.convenience;

import com.company.www.constant.staff.MailType;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Entity
@Table(name="mail")
@Getter
@Setter
public class Mail {
    @Id
    @Column(name="mail_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long mailId;

    @Column
    @NotEmpty
    @Length(min=1, max=200)
    private String title;

    @Column
    @NotEmpty
    @Length(min=1, max=2000)
    private String content;

    @ManyToOne
    @JoinColumn(name="send_staff_id")
    private Staff sendStaff;

    @ManyToOne
    @JoinColumn(name="receive_staff_id")
    private Staff receiveStaff;

    @Column
    private LocalDate send;

    @Column
    @Enumerated(EnumType.STRING)
    private MailType mailType;

    @Column
    private boolean isSendDeleted;

    @Column
    private boolean isReceiveDeleted;

    // 파일 입출력 관련 항목
}
