package com.company.www.service.post;

import com.company.www.constant.post.CommentType;
import com.company.www.dto.post.CommentDto;
import com.company.www.entity.post.Comment;
import com.company.www.entity.post.Post;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.post.CommentRepository;
import com.company.www.repository.post.PostRepository;
import com.company.www.repository.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class CommentService {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    public Comment createComment(Long id, Principal principal, String content, CommentType commentType){
        Post post = postRepository.findByPostId(id);
        Staff fromStaff = staffRepository.findByUserId(principal.getName());

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setFromStaff(fromStaff);
        comment.setToStaff(null);
        LocalDateTime createTime = LocalDateTime.now();
        createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        comment.setCreateTime(createTime);
        commentRepository.save(comment);

        return comment;
    }

    public Comment updateComment(Long com, CommentDto commentDto){
        Comment comment = commentRepository.findByCommentId(com);
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return comment;
    }

    public void deleteComment(Long id, Long com){
        Post post = postRepository.findByPostId(id);
        Comment deleted = commentRepository.findByCommentId(com);

        commentRepository.delete(deleted);
        /*
        if(comment.getParent() != null) {
            Comment parent = comment.getParent();
            parent.getChild().remove(comment);
            commentRepository.delete(comment);
            if(parent.getChild().isEmpty() && parent.getContent().equals("삭제된 댓글입니다.")){
                commentRepository.delete(parent);
            }
            else {
                commentRepository.save(parent);
            }
        }
        else{
            if(comment.getChild().isEmpty()) {
                post.getComment().remove(comment);
                postRepository.save(post);
                commentRepository.delete(comment);
            }
            else{
                comment.setContent("삭제된 댓글입니다.");
                commentRepository.save(comment);
            }
        }
         */
    }

    public Comment createReply(Long id, Long com, Principal principal, CommentDto commentDto, CommentType commentType){
        Staff fromStaff = staffRepository.findByUserId(principal.getName());
        Post post = postRepository.findByPostId(id);
        Comment parentComment = commentRepository.findByCommentId(com);

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setPost(post);
        comment.setFromStaff(fromStaff);
        comment.setToStaff(commentDto.getToStaff());
        LocalDateTime createTime = LocalDateTime.now();
        createTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분 ss초"));
        comment.setCreateTime(createTime);
        commentRepository.save(comment);

        return comment;
    }

    public Comment updateReply(Long rep, CommentDto commentDto){
        Comment comment = commentRepository.findByCommentId(rep);
        comment.setContent(commentDto.getContent());
        comment.setToStaff(comment.getToStaff());
        commentRepository.save(comment);
        return comment;
    }

    public void deleteReply(Long com, Long rep){
        Comment reply = commentRepository.findByCommentId(rep);
        commentRepository.delete(reply);
    }
}