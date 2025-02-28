package com.company.www.dto.post;

import com.company.www.constant.post.PostType;
import com.company.www.entity.belong.Role;
import com.company.www.entity.post.Comment;
import com.company.www.entity.staff.Staff;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private PostType postType;
    private Staff staff;
    private Role role;
    private List<Comment> comment;
}