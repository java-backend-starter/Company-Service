package com.company.www.repository.post;

import com.company.www.constant.post.PostType;
import com.company.www.entity.belong.Role;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByPostTypeAndRoleOrderByPostIdDesc(Pageable pageable, PostType postType, Role role);

    List<Post> findAllByPostTypeAndRoleOrderByPostIdDesc(PostType postType, Role role);

    List<Post> findAllByPostTypeAndStaffOrderByPostIdDesc(PostType postType, Staff staff);

    Post findByTitle(String title);

    Post findByPostId(Long id);

    Post save(Post post);

    void delete(Post post);
}