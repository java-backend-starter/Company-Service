package com.company.www.repository.post;

import com.company.www.entity.post.Comment;
import com.company.www.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByCommentId(Long id);

    List<Comment> findByPost(Post post);

    Comment save(Comment comment);

    void delete(Comment comment);

}