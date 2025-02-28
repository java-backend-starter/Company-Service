package com.company.www.service.post;

import com.company.www.constant.post.PostType;
import com.company.www.constant.staff.Gender;
import com.company.www.entity.belong.Role;
import com.company.www.entity.post.Comment;
import com.company.www.entity.post.Post;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.belong.*;
import com.company.www.repository.post.CommentRepository;
import com.company.www.repository.post.PostRepository;
import com.company.www.repository.staff.StaffRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {
    @Autowired
    StaffRepository staffRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;


    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PasswordEncoder passwordEncoder;



    @Transactional
    synchronized public Staff makeStaff(String id, String name, Gender gender){
        Staff staff = new Staff();

        LocalDate birthday = LocalDate.of(1997, 1, 1);
        birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate start = LocalDate.of(2020, 1, 1);
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        staff.setUserId(id);
        staff.setPassword(passwordEncoder.encode("qwer1234"));
        staffRepository.save(staff);

        staff.setStaffName(name);
        staff.setGender(gender);
        staff.setBirthDate(birthday);
        staff.setEmail("ascia333@naver.com");
        staff.setAddress("주소");
        staff.setContact("연락처");
        staff.setEmployDate(start);
        staff.setResign(null);

        staff.setRole(roleRepository.findByRoleName("경영진"));
        staff.setPosition(positionRepository.findByPositionName("사장"));
        staff.setDepartment(departmentRepository.findByDepartmentName("경영진"));
        staff.setSection(sectionRepository.findBySectionName("경영진"));
        return staffRepository.save(staff);
    }

    @Transactional
    synchronized public Post makePost(Staff staff, int index){
        Role role = roleRepository.findByRoleName("미정");
        Post post = new Post();

        post.setTitle("가나다" + index);
        post.setContent("내용 기록 : 가나다" + index);
        post.setPostType(PostType.NOTICE);
        post.setRole(role);
        post.setComment(new ArrayList<>());
        post.setStaff(staff);
        LocalDateTime createTime = LocalDateTime.now();
        createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        post.setCreateTime(createTime);

        return postRepository.save(post);
    }

    @Transactional
    synchronized public Comment makeComment(Post post, Staff fromStaff, String content){
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setFromStaff(fromStaff);
        comment.setCreateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Transactional
    synchronized public Comment makeReply(Post post, Comment parent, Staff fromStaff, String toStaff, String content){
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        comment.setFromStaff(fromStaff);
        comment.setToStaff(toStaff);
        comment.setCreateTime(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @DisplayName("게시글 댓글 달기 테스트")
    @Test
    @Transactional
    public void createComment(){
        Staff from = makeStaff("ascia33", "홍길동", Gender.MALE);
        Staff to = makeStaff("ascia44", "홍길순", Gender.FEMALE);
        Post post = makePost(from, 1);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent("가나다");
        comment.setFromStaff(from);
        comment.setToStaff(to.getStaffName());
        comment.setCreateTime(LocalDateTime.now());

        Comment created = commentRepository.save(comment);

        assertEquals(comment, created);
    }

    @DisplayName("게시글에서 여러 개의 댓글 달기 테스트")
    @Test
    @Transactional
    public void createManyComment(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE);
        Staff staff3 = makeStaff("ascia55", "홍길", Gender.MALE);
        Staff staff4 = makeStaff("ascia66", "홍길자", Gender.FEMALE);

        String staff1Name = staff1.getStaffName();
        String staff2Name = staff2.getStaffName();
        String staff3Name = staff3.getStaffName();
        String staff4Name = staff4.getStaffName();

        Post post = makePost(staff1, 1);
        Comment comment1 = makeComment(post, staff1, "가나다");
        Comment comment2 = makeReply(post, comment1, staff2, staff1Name, "답글 : 가나다");
        Comment comment3 = makeReply(post, comment1, staff3, staff1Name, "답글 : 다나가");
        Comment comment4 = makeReply(post, comment1, staff4, staff1Name, "답글 : 라마바");

        List<Comment> commentList = commentRepository.findAll();
        for(Comment c : commentList){
            String comment = (c.getToStaff() == null ? "" : c.getToStaff()) + " " + c.getContent();
            System.out.println("작성자 : " + c.getFromStaff().getStaffName()  +
                    " 내용 : " + comment);
        }
    }

    @DisplayName("게시글에서 댓글 수정 테스트")
    @Test
    @Transactional
    public void updateComment(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE);

        Post post = makePost(staff1, 1);
        Comment comment = makeComment(post, staff1, "가나다");

        System.out.println("작성자 : " + comment.getFromStaff().getStaffName() + ", 답글 대상 : " + comment.getToStaff() +
                ", 내용 : "  + comment.getContent());

        comment.setContent("댓글 수정 : 다나가");
        Comment updated = commentRepository.save(comment);

        System.out.println("comment == updated? " + comment.getContent().equals(updated.getContent()));
        System.out.println("작성자 : " + updated.getFromStaff().getStaffName() + ", 답글 대상 : " + updated.getToStaff() +
                 ", 내용 : "  + updated.getContent());
    }

    @DisplayName("게시글에서 댓글 삭제 테스트")
    @Test
    @Transactional
    public void deleteCommentTest(){
        Staff from = makeStaff("ascia33", "홍길동", Gender.MALE);
        Staff to = makeStaff("ascia44", "홍길순", Gender.FEMALE);
        Post post = makePost(from, 1);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent("가나다");
        comment.setFromStaff(from);
        comment.setToStaff(to.getStaffName());
        comment.setCreateTime(LocalDateTime.now());

        Comment created = commentRepository.save(comment);
        System.out.println("작성자 : " + comment.getFromStaff().getStaffName() + ", 답글 대상 : " + comment.getToStaff() +
                 ", 내용 : "  + comment.getContent());
        commentRepository.delete(created);

        System.out.println("댓글 삭제 여부 : " + (commentRepository.findByCommentId(created.getCommentId()) == null));
    }

    public void deleteComment(Comment deleted){
        commentRepository.delete(deleted);
    }

    public void displayComment(Comment comment){
        System.out.println("작성자 : " + comment.getFromStaff().getStaffName());
        System.out.println("답글 대상자 : " + comment.getToStaff());
        System.out.println("내용 : " + comment.getContent());
        System.out.println("\n----------\n");
    }

    @DisplayName("게시글에서 댓글 삭제 테스트2")
    @Test
    @Transactional
    public void deleteCommentTest2(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE);

        String staff1Name = staff1.getStaffName();
        String staff2Name = staff2.getStaffName();
        String staff3Name = staff3.getStaffName();

        Post post = makePost(staff1, 1);

        Comment comment1 = makeComment(post, staff1, "가나다");
        Comment comment2 = makeReply(post, comment1, staff2, staff1Name, "답글 : 가나다");

        deleteComment(comment1);

        Comment comment3 = commentRepository.findByCommentId(comment1.getCommentId());
        Comment comment4 = commentRepository.findByCommentId(comment2.getCommentId());

        displayComment(comment3);
        displayComment(comment4);

    }


}