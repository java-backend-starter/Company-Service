package com.company.www.controller.boardController;

import com.company.www.constant.post.CommentType;
import com.company.www.constant.post.PostType;
import com.company.www.converter.Converter;
import com.company.www.dto.post.CommentDto;
import com.company.www.dto.post.PostDto;
import com.company.www.entity.post.Post;
import com.company.www.service.staff.StaffInfoService;
import com.company.www.service.post.CommentService;
import com.company.www.service.post.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class InformationTechnologyController {

    @Autowired
    StaffInfoService staffInfoService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    private final static String INFORMATION_TECHNOLOGY_LINK = "/information";

    @GetMapping(value="/information")
    public String allInformationPost(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        postService.AllPost(page, "IT부 게시판", INFORMATION_TECHNOLOGY_LINK, false, PostType.NORMAL, "IT개발자", model);
        staffInfoService.getInfo(model, principal);
        model.addAttribute("backLink", "/");
        return "/post/post";
    }

    @GetMapping(value="/information/write")
    public String writeEditor(Model model){
        postService.createPostPage("IT부 게시글 작성", INFORMATION_TECHNOLOGY_LINK, model);
        return "/post/post_write";
    }
    
    @PostMapping(value="/information/write/create")
    public String createInformationPost(PostDto postDto, Principal principal, RedirectAttributes attributes){
        postService.createPost(principal, postDto, PostType.NORMAL, "IT개발자");
        attributes.addFlashAttribute("alertData", "게시글 작성이 완료되었습니다.");
        return "redirect:/information";
    }

    @GetMapping(value="/information/postId={postId}")
    public String detailedInformationPost(@PathVariable("postId") Long postId, Model model, Principal principal){
        postService.readPost(postId, "IT부 게시글", INFORMATION_TECHNOLOGY_LINK, model, principal);
        return "/post/post_content";
    }

    @GetMapping(value="/information/postId={postId}/update")
    public String updateEditor(@PathVariable("postId") Long postId, Model model){
        postService.updatePostPage(postId,"IT부 게시글 편집", INFORMATION_TECHNOLOGY_LINK, model);
        return "/post/post_write";
    }

    @PostMapping(value="/information/postId={postId}/update")
    public String updateInformationPost(@PathVariable("postId") Long postId, PostDto postDto, RedirectAttributes attributes){
        Post post = postService.updatePost(postId, postDto);
        attributes.addFlashAttribute("alertData", ("수정 완료되었습니다. 제목 : " + post.getTitle()));
        return ("redirect:/information/postId=" + postId);
    }

    @GetMapping(value="/information/postId={postId}/delete")
    public String deleteInformationPost(@PathVariable("postId") Long postId, RedirectAttributes attributes){
        String isDeleted = postService.deletePost(postId);
        if(isDeleted != null) {
            attributes.addFlashAttribute("alertData", "삭제 완료되었습니다. 제목 : " + isDeleted);
        }
        return "redirect:/information";
    }

    @PostMapping(value="/information/postId={postId}/comment")
    public String createComment(@PathVariable("postId") Long postId, CommentDto commentDto, Principal principal){
        commentService.createComment(postId, principal, commentDto.getContent(), CommentType.ORIGINAL);
        return "redirect:/information/postId=" + postId;
    }

    @GetMapping(value="/information/postId={postId}/comment/commentId={commentId}/create")
    public String createReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model, Principal principal){
        postService.createReplyPage(postId, commentId, "IT부 게시글", INFORMATION_TECHNOLOGY_LINK, model, principal);
        return "/post/post_content";
    }
    @PostMapping(value="/information/{id}/comment/{com}/create")
    public String createReply(@PathVariable("id") Long id, @PathVariable("com") Long com, CommentDto commentDto, Principal principal){
        commentService.createReply(id, com, principal, commentDto, CommentType.REPLY);
        return "redirect:/information/postId=" + id;
    }

    @GetMapping(value="/information/postId={postId}/comment/commentId={commentId}/update")
    public String updateCommentPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model){
        postService.updateCommentPage(postId, commentId, "IT부 게시글", INFORMATION_TECHNOLOGY_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/information/postId={postId}/comment/commentId={commentId}/update")
    public String updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateComment(commentId, commentDto);
        attributes.addFlashAttribute("alertData", "댓글 수정이 완료되었습니다.");
        return "redirect:/information/postId=" + postId;
    }
    @GetMapping(value="/information/postId={postId}/comment/commentId={commentId}/replyId={replyId}/update")
    public String updateReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, Model model){
        postService.updateReplyPage(postId, commentId, replyId, "IT부 게시글", INFORMATION_TECHNOLOGY_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/information/postId={postId}/comment/commentId={commentId}/replyId={replyId}/update")
    public String updateReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateReply(replyId, commentDto);
        attributes.addFlashAttribute("alertData", "답글 수정이 완료되었습니다.");
        return "redirect:/information/postId=" + postId;
    }

    @GetMapping(value="/information/postId={postId}/comment/commentId={commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, RedirectAttributes attributes){
        commentService.deleteComment(postId, commentId);
        attributes.addFlashAttribute("alertData", "댓글 삭제가 완료되었습니다.");
        return "redirect:/information/postId=" + postId;
    }
    @GetMapping(value="/information/postId={postId}/comment/commentId={commentId}/{replyId}/delete")
    public String deleteReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, RedirectAttributes attributes){
        commentService.deleteReply(commentId, replyId);
        attributes.addFlashAttribute("alertData", "답글 삭제가 완료되었습니다.");
        return "redirect:/information/postId=" + postId;
    }
}