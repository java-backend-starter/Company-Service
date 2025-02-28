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
public class NoticeController {

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    StaffInfoService staffInfoService;

    private final static String NOTICE_LINK = "/notice";

    @GetMapping(value="/notice")
    public String allNoticePost(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        postService.AllPost(page, "공지사항", "/notice", true, PostType.NOTICE, "미정", model);
        staffInfoService.getInfo(model, principal);
        model.addAttribute("backLink", "/");
        model.addAttribute("isNotice", true);
        return "/post/post";
    }

    @GetMapping(value="/notice/write")
    public String writeEditor(Model model){
        postService.createPostPage("공지사항 작성", NOTICE_LINK,  model);
        return "/post/post_write";
    }

    @PostMapping(value="/notice/write/create")
    public String createNoticePost(PostDto postDto, Principal principal, RedirectAttributes attributes){
        postService.createPost(principal, postDto, PostType.NOTICE, "미정");
        attributes.addFlashAttribute("alertData", "공지사항 작성이 완료되었습니다.");
        return "redirect:/notice";
    }

    @GetMapping(value="/notice/postId={postId}")
    public String detailedNoticePost(@PathVariable("postId") Long postId, Model model, Principal principal){
        postService.readPost(postId, "공지사항", NOTICE_LINK, model, principal);
        return "/post/post_content";
    }

    @GetMapping(value="/notice/postId={postId}/update")
    public String updateEditor(@PathVariable("postId") Long postId, Model model){
        postService.updatePostPage(postId, "공지사항 편집", NOTICE_LINK, model);
        return "/post/post_write";
    }

    @PostMapping(value="/notice/postId={postId}/update")
    public String updateNoticePost(@PathVariable("postId") Long postId, PostDto postDto, RedirectAttributes attributes){
        Post post = postService.updatePost(postId, postDto);
        attributes.addFlashAttribute("alertData", ("수정 완료되었습니다. 제목 : " + post.getTitle()));
        return ("redirect:/notice/postId=" + postId);
    }

    @GetMapping(value="/notice/postId={postId}/delete")
    public String deleteNoticePost(@PathVariable("postId") Long postId, RedirectAttributes attributes){
        String isDeleted = postService.deletePost(postId);
        if(isDeleted != null) {
            attributes.addFlashAttribute("alertData", "삭제 완료되었습니다. 제목 : " + isDeleted);
        }
        return "redirect:/notice";
    }

    @PostMapping(value="/notice/postId={postId}/comment")
    public String createComment(@PathVariable("postId") Long postId, CommentDto commentDto, Principal principal){
        commentService.createComment(postId, principal, commentDto.getContent(), CommentType.ORIGINAL);
        return "redirect:/notice/postId=" + postId;
    }

    @GetMapping(value="/notice/postId={postId}/comment/commentId={commentId}/create")
    public String createReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model, Principal principal){
        postService.createReplyPage(postId, commentId, "공지사항", NOTICE_LINK, model, principal);
        return "/post/post_content";
    }

    @PostMapping(value="/notice/postId={postId}/comment/commentId={commentId}/create")
    public String createReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, CommentDto commentDto, Principal principal){
        commentService.createReply(postId, commentId, principal, commentDto, CommentType.REPLY);
        return "redirect:/notice/postId=" + postId;
    }

    @GetMapping(value="/notice/postId={postId}/comment/commentId={commentId}/update")
    public String updateCommentPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Model model){
        postService.updateCommentPage(postId, commentId, "공지사항", NOTICE_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/notice/postId={postId}/comment/commentId={commentId}/update")
    public String updateComment(@PathVariable("postId") Long postId, @PathVariable("commentIdm") Long commentId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateComment(commentId, commentDto);
        attributes.addFlashAttribute("alertData", "댓글 수정이 완료되었습니다.");
        return "redirect:/notice/postId=" + postId;
    }
    @GetMapping(value="/notice/postId={postId}/comment/commentId={commentId}/replyId={replyId}/update")
    public String updateReplyPage(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, Model model){
        postService.updateReplyPage(postId, commentId, replyId, "공지사항", NOTICE_LINK, model);
        return "/post/post_content";
    }
    @PostMapping(value="/notice/postId={postId}/comment/commentId={commentId}/replyId={replyId}/update")
    public String updateReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, CommentDto commentDto, RedirectAttributes attributes){
        commentService.updateReply(replyId, commentDto);
        attributes.addFlashAttribute("alertData", "답글 수정이 완료되었습니다.");
        return "redirect:/notice/postId=" + postId;
    }

    @GetMapping(value="/notice/postId={postId}/comment/commentId={commentId}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, RedirectAttributes attributes){
        commentService.deleteComment(postId, commentId);
        attributes.addFlashAttribute("alertData", "댓글 삭제가 완료되었습니다.");
        return "redirect:/notice/postId=" + postId;
    }
    @GetMapping(value="/notice/postId={postId}/comment/commentId={commentId}/replyId={replyId}/delete")
    public String deleteReply(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @PathVariable("replyId") Long replyId, RedirectAttributes attributes){
        commentService.deleteReply(commentId, replyId);
        attributes.addFlashAttribute("alertData", "답글 삭제가 완료되었습니다.");
        return "redirect:/notice/postId=" + postId;
    }
}