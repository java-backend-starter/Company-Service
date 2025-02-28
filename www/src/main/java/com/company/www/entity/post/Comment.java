package com.company.www.entity.post;

import com.company.www.constant.post.CommentType;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="comments")
@Getter
@Setter

public class Comment {
    @Id
    @Column(name="comment_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long commentId;

    @Column
    @Length(min=1, max=1000)
    @NotBlank
    private String content;

    @Column
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name="from_staff_id")
    private Staff fromStaff;

    @Column(name="to_staff")
    private String toStaff;

    @Column(name="reply_size")
    private int replySize;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    /*
    댓글과 답글을 트리 형식으로 CRUD를 완전히 구현 X
    불완전하지만 간접적으로 구현 예정.

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Comment parent;

    @OneToMany(mappedBy="parent", orphanRemoval = true)
    private List<Comment> child;

     */
}