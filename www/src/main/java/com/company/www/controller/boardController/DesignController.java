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
public class DesignController {

    @Autowired
    StaffInfoService staffInfoService;
    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

    private final static String DESIGN_LINK = "/design";

    @GetMapping(value="/design")
    public String allDesignPost(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        postService.AllPost(page, "디자인부 게시판", DESIGN_LINK, false, PostType.NORMAL, "디자인", model);
        staffInfoService.getInfo(model, principal);
        model.addAttribute("backLink", "/");
        return "/post/post";
    }

    @GetMapping(value="/design/write")
    public String writeEditor(Model model){
        postService.createPostPage("디자인부 게시글 작성", DESIGN_LINK, model);
        return "/post/post_write";
    }

    @PostMapping(value="/design/write/create")
    public String createDesignPost(PostDto postDto, Principal principal, RedirectAttributes attributes){
        postService.createPost(principal, postDto, PostType.NORMAL, "디자인");
        attributes.addFlashAttribute("alertData", "게시글 작성이 완료되었습니다.");
        return "redirect:/design";
    }

    @GetMapping(value="/design/postId={postId}")
    public String detailedDesignPost(@PathVariable("postId") Long postId, Model model, Principal principal){
        postService.readPost(postId,"디자인부 게시글", DESIGN_LINK, model, principal);
        return "/post/post_content";
    }

    @GetMapping(value="/design/postId={postId}/update")
    public String updateEditor(@PathVariable("id") Long id, Model model){
        postService.updatePostPage(id, "디자인부 게시글 편집", DESIGN_LINK, model);
        return "/post/post_write";
    }

    @PostMapping(value="/design/postId={postId}/update")
    public String updateDesignPost(@PathVariable("postId") Long postId, PostDto postDto, RedirectAttributes attributes){
        Post post = postService.updatePost(postId, postDto);
        attributes.addFlashAttribute("alertData", ("수정 완료되었습니다. 제목 : " + post.getTitle()));
        return ("redirect:/design/" + postId);
    }

    @GetMapping(value="/design/postId={postId}/delete")
    public String deleteDesignPost(@PathVariable("postId") Long postId, RedirectAttributes attributes){
        String isDeleted = postService.deletePost(postId);
        if(isDeleted != null) {
            attributes.addFlashAttribute("alertData", "삭제 완료되었습니다. 제목 : " + isDeleted);
        }
        return "redirect:/design";
    }

    @PostMapping(value="/design/postId={postId}/comment")
    public String createComment(@PathVariable("postId") Long postId, CommentDto commentDto, Principal principal){
        commentService.createComment(postId, principal, commentDto.getContent(), CommentType.ORIGINAL);
        return "redirect:/design/postId=" + postId;
    }

    @GetMapping(value="/design/postId={postId}/comment/commentId={commentId}/create")
    public String createReplyPage(@PathVariable("postId")Long postId, @PathVariable("commentId") Long commentId, Model model, Principal principal){
        postService.createReplyPage(postId, commentId, "디자인부 게시글", DESIGN_LINK, model, principal);
        return "/post/post_content";
    }

    @PostMapping(value="/design/postId={postId}/comment/commentId={commentId}/create")
    public String createReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, Principal principal){
        commentService.createReply(postId, commentId, principal, commentDto, CommentType.REPLY);
        return "redirect:/design/postId=" + postId;
    }

    @GetMapping(value="/design/postI={postId}/comment/{commentId}/update")
    public String updateCommentPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model){
        postService.updateCommentPage(postId, commentId,"디자인부 게시글", DESIGN_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/design/postI={postId}/comment/commentId={commentId}/update")
    public String updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateComment(commentId, commentDto);
        attributes.addFlashAttribute("alertData", "댓글 수정이 완료되었습니다.");
        return "redirect:/design/postId=" + postId;
    }
    @GetMapping(value="/design/postI={postId}/comment/{com}/replyId={replyId}/update")
    public String updateReplyPage(@PathVariable("id")Long id, @PathVariable("com") Long com, @PathVariable("rep") Long rep, Model model){
        postService.updateReplyPage(id, com, rep, "디자인부 게시글", DESIGN_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/design/postI={postId}/comment/commentId={commentId}/replyId={replyId}/update")
    public String updateReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("rep") Long rep, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateReply(rep, commentDto);
        attributes.addFlashAttribute("alertData", "답글 수정이 완료되었습니다.");
        return "redirect:/design/postId=" + postId;
    }

    @GetMapping(value="/design/postI={postId}/comment/commentId={commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, RedirectAttributes attributes){
        commentService.deleteComment(postId, commentId);
        attributes.addFlashAttribute("alertData", "댓글 삭제가 완료되었습니다.");
        return "redirect:/design/postId=" + postId;
    }
    @GetMapping(value="/design/postI={postId}/comment/commentId={commentId}/replyId={replyId}/delete")
    public String deleteReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, RedirectAttributes attributes){
        commentService.deleteReply(commentId, replyId);
        attributes.addFlashAttribute("alertData", "답글 삭제가 완료되었습니다.");
        return "redirect:/design/postId=" + postId;
    }
}