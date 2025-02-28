package com.company.www.entity.post;

import com.company.www.constant.post.PostType;
import com.company.www.entity.belong.Role;
import com.company.www.entity.staff.Staff;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="post")
@Getter
@Setter

public class Post {
    @Id
    @Column(name="post_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long postId;

    @Column
    @NotEmpty
    @Length(min=1, max=100)
    private String title;

    @Column
    @NotEmpty
    @Length(min=1, max=2000)
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    private PostType postType;

    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @OneToMany(mappedBy="post")
    private List<Comment> comment;
}