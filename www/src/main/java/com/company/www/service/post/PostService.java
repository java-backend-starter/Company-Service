package com.company.www.service.post;

import com.company.www.constant.post.PostType;
import com.company.www.converter.Converter;
import com.company.www.dto.post.PostDto;
import com.company.www.entity.belong.Role;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.post.Comment;
import com.company.www.entity.post.Post;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.post.CommentRepository;
import com.company.www.repository.post.PostRepository;
import com.company.www.repository.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    public Post createPost(Principal principal, PostDto postDto, PostType postType, String roleName){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = roleRepository.findByRoleName(roleName);
        Post post = new Post();

        String title = postDto.getTitle(), content = postDto.getContent();
        post.setTitle(title);
        post.setContent(content);
        post.setPostType(postType);
        post.setRole(role);
        post.setComment(new ArrayList<>());
        post.setStaff(staff);
        LocalDateTime createTime = LocalDateTime.now();
        createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        post.setCreateTime(createTime);

        postRepository.save(post);
        return post;
    }

    public Page<Post> readPosts(Pageable pageable, PostType postType, String roleName){
        Role role = roleRepository.findByRoleName(roleName);
        return postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(pageable, postType, role);
    }

    public Post updatePost(Long id, PostDto postDto){
        Post post = postRepository.findByPostId(id);
        String title = postDto.getTitle(), content = postDto.getContent();
        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);
        return post;
    }

    public String deletePost(Long id){
        Post post = postRepository.findByPostId(id);
        if(post == null){
            return null;
        }
        String title = post.getTitle();
        List<Comment> commentList = post.getComment();
        for(Comment comment : commentList){
            commentRepository.delete(comment);
        }
        post.setComment(null);
        postRepository.delete(post);
        return title;
    }

    public void AllPost(int page, String title, String link, Boolean isAuthority, PostType postType, String roleName, Model model){
        Page<Post> paging = readPosts(PageRequest.of(page, 10), postType, roleName);
        model.addAttribute("paging", paging);
        model.addAttribute("headerTitle", title);
        model.addAttribute("writeLink", (link + "/write"));
        model.addAttribute("postLink", link + "/postId=");
        model.addAttribute("isAuthority", isAuthority);
        model.addAttribute("isPost", true);
    }

    // 게시글을 추가하기 위한 페이지
    public void createPostPage(String title, String link, Model model){
        model.addAttribute("headerTitle", title);
        model.addAttribute("link", link + "/write/create");
        model.addAttribute("backLink", link);
    }

    // 게시글을 수정하기 위한 페이지
    public void updatePostPage(Long id, String title, String link, Model model){
        Post post = postRepository.findByPostId(id);
        model.addAttribute("post", post);
        model.addAttribute("headerTitle", title);
        model.addAttribute("link", (link + "/postId=" + id + "/update"));
        model.addAttribute("backLink", (link + "/postId=" + id));
    }

    // 글 읽기 + 댓글 관련 처리
    public void readPost(Long postId, String headerTitle, String postLink, Model model, Principal principal){
        Post post = postRepository.findByPostId(postId);
        List<Comment> commentList = commentRepository.findByPost(post);
        Staff staff = staffRepository.findByUserId(principal.getName());

        String pLink = postLink + "/postId=" + postId;
        String cLink = postLink + "/postId=" + postId + "/comment/commentId=";
        String commentState = "댓글을 작성하세요";

        postModelAttributes(
                model,
                Converter.makeStringArray(
                        staff.getStaffName(),
                        headerTitle,
                        postLink,
                        pLink,
                        cLink,
                        commentState
                )
        );
        model.addAttribute("post", post);
        model.addAttribute("comment", commentList);
        model.addAttribute("isUpdate", false);
        model.addAttribute("isReply", false);
    }

    public void createReplyPage(Long postId, Long commentId, String headerTitle, String postLink, Model model, Principal principal){
        Post post = postRepository.findByPostId(postId);
        Staff fromStaff = staffRepository.findByUserId(principal.getName());
        Staff toStaff = commentRepository.findByCommentId(commentId).getFromStaff();

        String pLink = postLink + "/postId=" + postId;
        String cLink = postLink + "/postId=" + postId + "/comment/commentId=" + commentId + "/create";
        String commentState = "답글을 작성하세요";

        postModelAttributes(
                model,
                Converter.makeStringArray(
                        fromStaff.getStaffName(),
                        headerTitle,
                        postLink,
                        pLink,
                        cLink,
                        commentState
                )
        );
        model.addAttribute("post", post);
        model.addAttribute("comment", post.getComment());
        model.addAttribute("toStaff", toStaff.getStaffName());
        model.addAttribute("isUpdate", false);
        model.addAttribute("isReply", true);
    }

    public void updateCommentPage(Long postId, Long commentId, String headerTitle, String postLink, Model model){
        Post post = postRepository.findByPostId(postId);
        Comment comment = commentRepository.findByCommentId(commentId);
        String pLink = postLink + "/postId=" + postId;
        String cLink;
        String commentState;

        cLink = postLink + "/postId=" + postId + "/comment/commentId=" + commentId + "/update";
        commentState = "댓글을 수정하세요";


        postModelAttributes(
                model,
                Converter.makeStringArray(
                        comment.getFromStaff().getStaffName(),
                        headerTitle,
                        postLink,
                        pLink,
                        cLink,
                        commentState
                )
        );
        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        model.addAttribute("isUpdate", true);
        model.addAttribute("isReply", false);
    }

    public void updateReplyPage(Long postId, Long commentId, Long replyId, String headerTitle, String postLink, Model model){
        Post post = postRepository.findByPostId(postId);
        Comment comment = commentRepository.findByCommentId(commentId);
        String pLink = postLink + "/" + postId;
        String cLink;
        String commentState;

        cLink = postLink + "/postId=" + postId + "/comment/commentId=" + commentId + "/replyId=" + replyId + "/update";
        commentState = "답글을 수정하세요";

        postModelAttributes(
                model,
                Converter.makeStringArray(
                        comment.getFromStaff().getStaffName(),
                        headerTitle,
                        postLink,
                        pLink,
                        cLink,
                        commentState
                )
        );
        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        model.addAttribute("isUpdate", true);
        model.addAttribute("isReply", false);
    }

    // 게시글의 공통적인 사항을 모델이 저장
    public void postModelAttributes(Model model, String[] sValues){
        model.addAttribute("staffName", sValues[0]);
        model.addAttribute("headerTitle", sValues[1]);
        model.addAttribute("link", sValues[2]);
        model.addAttribute("detailLink",sValues[3]);
        model.addAttribute("commentLink", sValues[4]);
        model.addAttribute("commentState", sValues[5]);
    }
}