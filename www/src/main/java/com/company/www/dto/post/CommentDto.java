package com.company.www.dto.post;

import com.company.www.constant.post.CommentType;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.post.Comment;
import com.company.www.entity.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentDto {
    private Long commentId;
    private String content;
    private CommentType commentType;
    private LocalDateTime createTime;
    private Staff fromStaff;
    private String toStaff;
    private Post post;
    private Boolean isReply;
    private Comment parent;
    private List<Comment> child;
}