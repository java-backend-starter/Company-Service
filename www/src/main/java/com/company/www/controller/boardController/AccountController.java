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
public class AccountController {

    @Autowired
    StaffInfoService staffInfoService;
    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

    private final static String ACCOUNT_LINK = "/account";

    @GetMapping(value="/account")
    public String allAccountPost(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        postService.AllPost(page, "회계부 게시판", ACCOUNT_LINK , false, PostType.NORMAL, "회계", model);
        staffInfoService.getInfo(model, principal);
        model.addAttribute("backLink", "/");
        return "/post/post";
    }

    @GetMapping(value="/account/write")
    public String writeEditor(Model model){
        postService.createPostPage("회계부 게시글 작성", ACCOUNT_LINK,  model);
        return "/post/post_write";
    }
    @PostMapping(value="/account/write/create")
    public String createAccountPost(PostDto postDto, Principal principal, RedirectAttributes attributes){
        postService.createPost(principal, postDto, PostType.NORMAL, "회계");
        attributes.addFlashAttribute("alertData", "게시글 작성이 완료되었습니다.");
        return "redirect:/account";
    }

    @GetMapping(value="/account/postId={postId}")
    public String detailedAccountPost(@PathVariable("postId") Long postId, Model model, Principal principal){
        postService.readPost(postId, "회계부 게시글", ACCOUNT_LINK, model, principal);
        return "/post/post_content";
    }

    @GetMapping(value="/account/update/postId={postId}")
    public String updateEditor(@PathVariable("postId") Long postId, Model model){
        postService.updatePostPage(postId, "회계부 게시글 편집", ACCOUNT_LINK , model);
        return "/post/post_write";
    }
    @PostMapping(value="/account/update/postId={postId}")
    public String updateAccountPost(@PathVariable("postId") Long postId, PostDto postDto, RedirectAttributes attributes){
        Post post = postService.updatePost(postId, postDto);
        attributes.addFlashAttribute("alertData", ("수정 완료되었습니다. 제목 : " + post.getTitle()));
        return ("redirect:/account/postId=" + postId);
    }

    @GetMapping(value="/account/delete/postId={postId}")
    public String deleteAccountPost(@PathVariable("postId") Long postId, RedirectAttributes attributes){
        String isDeleted = postService.deletePost(postId);
        if(isDeleted != null) {
            attributes.addFlashAttribute("alertData", "삭제 완료되었습니다. 제목 : " + isDeleted);
        }
        return "redirect:/account";
    }

    @PostMapping(value="/account/postId={postId}/comment")
    public String createComment(@PathVariable("postId") Long postId, CommentDto commentDto, Principal principal){
        commentService.createComment(postId, principal, commentDto.getContent(), CommentType.ORIGINAL);
        return "redirect:/account/postId=" + postId;
    }

    @GetMapping(value="/account/postId={postId}/comment/create/commentId={commentId}")
    public String createReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model, Principal principal){
        postService.createReplyPage(postId, commentId, "회계부 게시글", ACCOUNT_LINK, model, principal);
        return "/post/post_content";
    }

    @PostMapping(value="/account/postId={postId}/comment/create/commentId={commentId}")
    public String createReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, Principal principal){
        commentService.createReply(postId, commentId, principal, commentDto, CommentType.REPLY);
        return "redirect:/account/postId=" + postId;
    }

    @GetMapping(value="/account/postId={postId}/comment/update/commentId={commentId}")
    public String updateCommentPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model){
        postService.updateCommentPage(postId, commentId, "회계부 게시글", ACCOUNT_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/account/postId={postId}/comment/update/commentId={commentId}")
    public String updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateComment(commentId, commentDto);
        attributes.addFlashAttribute("alertData", "댓글 수정이 완료되었습니다.");
        return "redirect:/account/postId=" + postId;
    }
    @GetMapping(value="/account/postId={postId}/comment/update/commentId={commentId}/{replyId}")
    public String updateReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, Model model){
        postService.updateReplyPage(postId, commentId, replyId, "회계부 게시글", ACCOUNT_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/account/postId={postId}/comment/update/commentId={commentId}/{rep}/update")
    public String updateReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
                              @PathVariable("rep") Long rep, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateReply(rep, commentDto);
        attributes.addFlashAttribute("alertData", "답글 수정이 완료되었습니다.");
        return "redirect:/account/postId=" + postId;
    }

    @GetMapping(value="/account/postId={postId}/comment/delete/commentId={commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, RedirectAttributes attributes){
        commentService.deleteComment(postId, commentId);
        attributes.addFlashAttribute("alertData", "댓글 삭제가 완료되었습니다.");
        return "redirect:/account/postId=" + postId;
    }
    @GetMapping(value="/account/postId={postId}/comment/delete/commentId={commentId}/{replyId}/delete")
    public String deleteReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
                              @PathVariable("replyId") Long replyId, RedirectAttributes attributes){
        commentService.deleteReply(commentId, replyId);
        attributes.addFlashAttribute("alertData", "답글 삭제가 완료되었습니다.");
        return "redirect:/account/postId=" + postId;
    }
}