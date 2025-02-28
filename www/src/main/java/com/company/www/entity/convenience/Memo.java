package com.company.www.entity.convenience;

import com.company.www.constant.staff.MemoStatus;
import com.company.www.constant.staff.MemoType;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Entity
@Table(name="memo")
@Getter
@Setter

public class Memo {
    @Id
    @Column(name="memo_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long memoId;

    @Column
    @NotEmpty
    @Length(min=1, max=200)
    private String title;

    @Column
    @NotEmpty
    @Length(min=1, max=2000)
    private String content;

    @Column
    private MemoStatus memoStatus;

    @Column
    private MemoType memoType;

    @Column
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;
}
