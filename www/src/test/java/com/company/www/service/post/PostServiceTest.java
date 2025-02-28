package com.company.www.service.post;

import com.company.www.constant.post.PostType;
import com.company.www.constant.staff.Gender;
import com.company.www.entity.belong.Role;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

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

    public Staff makeStaff(){
        Staff staff = new Staff();

        LocalDate birthday = LocalDate.of(1997, 1, 1);
        LocalDate start = LocalDate.of(2020, 1, 1);

        staff.setUserId("ascia333");
        staff.setPassword(passwordEncoder.encode("qwer1234"));
        staffRepository.save(staff);

        staff.setStaffName("홍길동");
        staff.setGender(Gender.MALE);
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

    public Staff makeStaff(String id, String name, Gender gender){
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

    @DisplayName("게시글 생성 테스트")
    @Test
    @Transactional
    public void createPost(){
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE);
        Role role = roleRepository.findByRoleName("미정");
        Post post = new Post();

        post.setTitle("가나다");
        post.setContent("내용 기록 : 가나다");
        post.setPostType(PostType.NOTICE);
        post.setRole(role);
        post.setComment(new ArrayList<>());
        post.setStaff(staff);
        LocalDateTime createTime = LocalDateTime.now();
        createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        post.setCreateTime(createTime);

        Post created = postRepository.save(post);

        assertEquals(post, created, "일치하지 않습니다.");
    }

    // 게시글 테스트용
    public Post makePost(Staff staff, int index){
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

    @DisplayName("1명의 게시글 다수 생성 테스트")
    @Test
    @Transactional
    public void createManyPosts(){
        Staff staff = makeStaff();

        Post [] posts = new Post[20];
        for(int i = 0; i < posts.length; i++){
            posts[i] = makePost(staff, i);
        }

        boolean isStaffEquals = true;
        for (Post post : posts) {
            String staffId = post.getStaff().getUserId();
            if (!staffId.equals(staff.getUserId())) {
                isStaffEquals = false;
                break;
            }
        }
        System.out.println("동일인의 작성 여부: " + isStaffEquals);
        for(Post post : posts){
            System.out.println("title : " + post.getTitle() + ", content : " + post.getContent());
        }
    }

    @DisplayName("2명 이상의 게시글 다수 생성 테스트")
    @Test
    @Transactional
    public void createManyPostsByManyStaff(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE);
        Staff staff3 = makeStaff("ascia55", "홍길", Gender.MALE);
        Staff staff4 = makeStaff("ascia66", "홍길자", Gender.FEMALE);

        Post [] posts = new Post[20];
        for(int i = 0; i < posts.length; i++){
            if(i % 4 == 0){
                posts[i] = makePost(staff1, i);
            }
            else if(i % 4 == 1){
                posts[i] = makePost(staff2, i);
            }
            else if(i % 4 == 2){
                posts[i] = makePost(staff3, i);
            }
            else {
                posts[i] = makePost(staff4, i);
            }
        }

        boolean isStaff1Equals = true;
        boolean isStaff2Equals = true;
        boolean isStaff3Equals = true;
        boolean isStaff4Equals = true;

        for (int i = 0; i < posts.length; i += 4) {
            String staffId = posts[i].getStaff().getUserId();
            if (!staffId.equals(staff1.getUserId())) {
                isStaff1Equals = false;
                break;
            }
        }
        for(int i = 1; i < posts.length; i += 4){
            String staffId = posts[i].getStaff().getUserId();
            if (!staffId.equals(staff2.getUserId())) {
                isStaff2Equals = false;
                break;
            }
        }
        for(int i = 2; i < posts.length; i += 4){
            String staffId = posts[i].getStaff().getUserId();
            if (!staffId.equals(staff3.getUserId())) {
                isStaff3Equals = false;
                break;
            }
        }
        for(int i = 3; i < posts.length; i += 4){
            String staffId = posts[i].getStaff().getUserId();
            if (!staffId.equals(staff4.getUserId())) {
                isStaff4Equals = false;
                break;
            }
        }

        System.out.println("Staff1이 " + posts.length/4 +  "개 만들었는지 : " + isStaff1Equals);
        System.out.println("Staff2가 " + posts.length/4 +  "개 만들었는지 : " + isStaff2Equals);
        System.out.println("Staff3이 " + posts.length/4 +  "개 만들었는지 : " + isStaff3Equals);
        System.out.println("Staff4가 " + posts.length/4 +  "개 만들었는지 : " + isStaff4Equals);
        System.out.println();

        for(Post post : posts){
            System.out.println("staff : " + post.getStaff().getUserId() + ", title : " + post.getTitle() + ", content : " + post.getContent());
        }
    }

    @DisplayName("게시글 읽기 테스트")
    @Test
    @Transactional
    public void readPost(){
        Staff staff = makeStaff();
        Role role = roleRepository.findByRoleName("미정");

        Post [] posts = new Post[100];
        for(int i = 0; i < posts.length; i++){
            posts[i] = makePost(staff, i);
        }
        List<Post> postList = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NOTICE, role);
        Arrays.sort(posts, (a, b) -> (int) (b.getPostId() - a.getPostId()));

        for(int i = 0; i < posts.length; i++){
            System.out.println("제목 비교(원본 : " + posts[i].getTitle() + ", 저장본 : " + postList.get(i).getTitle() + ")");
        }
    }

    @DisplayName("게시글 수정 테스트")
    @Test
    @Transactional
    public void updatePost(){
        Staff staff = makeStaff();
        Role role = roleRepository.findByRoleName("미정");

        Post [] posts = new Post[100];
        for(int i = 0; i < posts.length; i++){
            posts[i] = makePost(staff, i);
        }
        List<Post> postList = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NOTICE, role);

        for(int i = 0; i < posts.length; i++){
            System.out.println("번호 : " + postList.get(i).getPostId() + " 제목 : " + postList.get(i).getTitle() + ", 내용 : " + postList.get(i).getContent());
        }

        for(long i = postList.size(); i > 0; i--){
            Post post = postRepository.findByPostId(i);
            post.setTitle("다나가" + i);
            post.setContent("내용 기록 : 다나가" + i);
        }

        for(int i = 0; i < posts.length; i++){
            System.out.println("번호 : " + postList.get(i).getPostId() + " 제목 : " + postList.get(i).getTitle() + ", 내용 : " + postList.get(i).getContent());
        }

    }

    @DisplayName("게시글 삭제 테스트")
    @Test
    @Transactional
    public void deletePosts(){
        Staff staff = makeStaff();
        Role role = roleRepository.findByRoleName("미정");

        Post [] posts = new Post[100];
        for(int i = 0; i < posts.length; i++){
            posts[i] = makePost(staff, i);
        }
        List<Post> postList = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NOTICE, role);

        System.out.println("isEmpty() : " + postList.isEmpty() + " size() : " + postList.size() + "\n");
        for(int i = 0; i < posts.length; i++){
            System.out.println("번호 : " + postList.get(i).getPostId() + " 제목 : " + postList.get(i).getTitle() + ", 내용 : " + postList.get(i).getContent());
        }

        System.out.println();
        for(Post post : postList){
            postRepository.delete(post);
        }
        postList = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NOTICE, role);
        System.out.println("isEmpty() : " + postList.isEmpty() + " size() : " + postList.size());
    }

}