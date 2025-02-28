package com.company.www.controller.workController;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.belong.DepartmentDto;
import com.company.www.dto.belong.PositionDto;
import com.company.www.dto.belong.RoleDto;
import com.company.www.dto.staff.StaffDto;
import com.company.www.dto.work.WorkDto;
import com.company.www.dto.work.human_resource.*;
import com.company.www.entity.belong.*;
import com.company.www.entity.work.human_reasource.*;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.repository.belong.*;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.*;
import com.company.www.repository.work.human_resource.*;
import com.company.www.service.staff.StaffInfoService;
import com.company.www.service.staff.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class HumanResourceWorkController {

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffService staffService;
    @Autowired
    StaffInfoService staffInfoService;

    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;

    @Autowired
    HolidayRepository holidayRepository;
    @Autowired
    CommuteRepository commuteRepository;
    @Autowired
    CommuteMonthlyRecordRepository commuteMonthlyRecordRepository;
    @Autowired
    RecruitRepository recruitRepository;
    @Autowired
    ApplicantRepository applicantRepository;
    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TraineeRepository traineeRepository;
    @Autowired
    TrainingAppraisalRepository trainingAppraisalRepository;
    @Autowired
    AppraisalRepository appraisalRepository;
    @Autowired
    PerformanceRepository performanceRepository;
    @Autowired
    PromotionChangeRepository promotionChangeRepository;
    @Autowired
    PayInfoRepository payInfoRepository;
    @Autowired
    PayRepository payRepository;
    @Autowired
    PayrollRepository payrollRepository;

    @GetMapping(value="/human/work")
    public String humanResourceWork(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("인사"));
        workTypes.removeAll(Converter.filter(workTypeRepository, "인사부 업무", "채용공고", "지원자", "선발결과",
                "교육 프로그램", "교육 대상자", "교육 평가", "실적", "승진&직무이동", "급여 정보", "급여 명세서"));
        model.addAttribute("headerTitle", "인사부 업무");
        model.addAttribute("workType", workTypes);
        model.addAttribute("backLink", "/");
        model.addAttribute("isWork", true);
        return "/work/work";
    }

    @GetMapping(value="/human/work/organization")
    public String organizationIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("부", "/organization/department");
        works.put("과", "/organization/section");
        works.put("팀", "/organization/team");

        works.put("부 현황", "/department/all");
        works.put("부 추가", "/department/insert");
        works.put("부 개편", "/department/update");
        works.put("과 현황", "/section/all");
        works.put("과 추가", "/section/insert");
        works.put("과 개편", "/section/update");
        works.put("팀 현황", "/team/all");
        works.put("팀 추가", "/team/insert");
        works.put("팀 개편", "/team/update");

        model.addAttribute("headerTitle", "인사부 업무-부서관리");
        model.addAttribute("backLink", "/human/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/department/all")
    public String allDepartment(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<Department> departments = departmentRepository.findAll();
        departments.remove(0);
        LinkedHashMap<Long, String> department = new LinkedHashMap<>();
        for(Department d : departments){
            department.put(d.getDepartmentId(), d.getDepartmentName());
        }

        model.addAttribute("headerTitle", "인사부 업무-부서관리-부서 현황");
        model.addAttribute("type", department);
        model.addAttribute("isType", true);
        model.addAttribute("backLink", "/human/work/department");
        model.addAttribute("detailedWorkLink", "/department/departmentId=");
        return "/work/work_detail_list";
    }

    @GetMapping(value="/department/departmentId={departmentId}")
    public String departmentInfo(@PathVariable("departmentId")Long departmentId, Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        Department department = departmentRepository.findByDepartmentId(departmentId);
        Role role = roleRepository.findByRoleName(
                Converter.convertToRoleName(department.getDepartmentName())
        );

        List<Staff> staffs = staffRepository.findAllByDepartment(department);

        model.addAttribute("headerTitle", department.getDepartmentName());
        model.addAttribute("backLink", "/department/all");
        model.addAttribute("department", department);
        if(!department.getSections().isEmpty()){
            model.addAttribute("section", department.getSections());
        }
        model.addAttribute("departmentStaff", staffs);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("deleteLink", "/department/delete/departmentId=" + departmentId);
            model.addAttribute("isDeleteAuthority", true);
            model.addAttribute("updateLink", "/department/update/departmentId=" + departmentId);
            model.addAttribute("isUpdateAuthority", true);
        }

        return "/work/human_resource/department";
    }
    @GetMapping(value="/department/insert")
    public String insertDepartmentPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = staff.getRole();
        model.addAttribute("headerTitle", "부서 추가");
        model.addAttribute("link", "/department/insert");
        model.addAttribute("backLink", "/human/work/department");
        model.addAttribute("isNew", true);
        if(role.getRoleName().equals("경영진")) {
            model.addAttribute("isInsertAuthority", true);
        }
        return "/work/human_resource/department";
    }
    @PostMapping(value="/department/insert")
    public String insertDepartment(DepartmentDto departmentDto, RedirectAttributes attributes){
        if(departmentDto.getDepartmentName().isEmpty()){
            attributes.addFlashAttribute("alertData", "부서 이름을 입력하세요.");
            return "redirect:/department/insert";
        }
        if(departmentDto.getDepartmentEnglishName().isEmpty()){
            attributes.addFlashAttribute("alertData", "부서 이름의 영문명을 입력하세요.");
            return "redirect:/department/insert";
        }
        Department department = new Department();
        department.setDepartmentName(departmentDto.getDepartmentName());
        String departmentLink = "/" + departmentDto.getDepartmentEnglishName().toLowerCase();
        department.setDepartmentEnglishName(departmentLink);
        departmentRepository.save(department);
        attributes.addFlashAttribute("alertData", "부서가 생성되었습니다.");
        return "redirect:/human/work/department";
    }

    @GetMapping(value="/department/update")
    public String updateDepartmentPage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff =staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Department> departments = departmentRepository.findAll();
            LinkedHashMap<Long, String> department = new LinkedHashMap<>();
            for (Department d : departments) {
                department.put(d.getDepartmentId(), d.getDepartmentName());
            }

            model.addAttribute("headerTitle", "인사부 업무-부서관리-부서 수정");
            model.addAttribute("type", department);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/department");
            model.addAttribute("detailedWorkLink", "/department/departmentId=");
            model.addAttribute("isUpdateAuthority", true);
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "수정 권한이 없습니다.");
            return "redirect:/human/work/department";
        }
    }

    @GetMapping(value="/department/update/departmentId={departmentId}")
    public String updateDepartmentInfoPage(@PathVariable("departmentId") Long departmentId, Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Department department = departmentRepository.findByDepartmentId(departmentId);
        Role role = roleRepository.findByRoleName(
                Converter.convertToRoleName(department.getDepartmentName())
        );
        List<Staff> staffs = staffRepository.findAllByDepartment(department);

        model.addAttribute("headerTitle", department.getDepartmentName());
        model.addAttribute("backLink", "/department/all");
        model.addAttribute("department", department);
        if(department.getSections() != null){
            model.addAttribute("section", department.getSections());
        }
        model.addAttribute("departmentStaff", staffs);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("isUpdateAuthority", true);
        }
        model.addAttribute("link", "/department/update/departmentId=" + departmentId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/department";
    }
    @PostMapping(value="/department/update/departmentId={departmentId}")
    public String updateDepartmentInfo(@PathVariable("departmentId") Long departmentId, DepartmentDto departmentDto, RedirectAttributes attributes){
        Department department = departmentRepository.findByDepartmentId(departmentId);
        department.setDepartmentName(departmentDto.getDepartmentName());
        departmentRepository.save(department);
        attributes.addFlashAttribute("alertData", "부서 개편이 완료되었습니다.");
        return "redirect:/department/departmentId=" + departmentId;
    }

    @GetMapping(value="/department/delete")
    public String deleteDepartmentPage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Department> departments = departmentRepository.findAll();
            LinkedHashMap<Long, String> department = new LinkedHashMap<>();
            for (Department d : departments) {
                department.put(d.getDepartmentId(), d.getDepartmentName());
            }

            model.addAttribute("headerTitle", "인사부 업무-부서관리-부서 삭제");
            model.addAttribute("type", department);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/department");
            model.addAttribute("detailedWorkLink", "/department/delete/departmentId=");
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "삭제 권한이 없습니다.");
            return "redirect:/human/work/department";
        }
    }
    @GetMapping(value="/department/delete/departmentId={departmentId}")
    public String deleteDepartment(@PathVariable("departmentId") Long departmentId, RedirectAttributes attributes){
        Department department = departmentRepository.findByDepartmentId(departmentId);
        if(department.getSections().isEmpty()) {
            departmentRepository.delete(department);
            attributes.addFlashAttribute("alertData", "부서가 삭제되었습니다.");
            return "redirect:/human/work/department";
        }
        else{
            attributes.addFlashAttribute("alertData", "부서 소속 과가 존재합니다.\n과를 삭제한 뒤에 부서를 삭제하세요.");
            return "redirect:/department/departmentId=" + departmentId;
        }
    }

    @GetMapping(value="/human/work/role")
    public String roleIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("직무 종류", "/role/all");
        works.put("직무 추가", "/role/insert");
        works.put("직무 개편", "/role/update");
        works.put("직무 삭제", "/role/delete");

        model.addAttribute("works", works);
        model.addAttribute("headerTitle", "인사부 업무-직무관리");
        model.addAttribute("backLink", "/human/work");

        return "/work/work_detail";
    }

    @GetMapping(value="/role/all")
    public String allRole(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<Role> roles = roleRepository.findAll();
        roles.remove(0);
        LinkedHashMap<Long, String> role = new LinkedHashMap<>();
        for(Role r : roles){
            role.put(r.getRoleId(), r.getRoleName());
        }

        model.addAttribute("headerTitle", "인사부 업무-직무관리-직무 종류");
        model.addAttribute("type", role);
        model.addAttribute("isType", true);
        model.addAttribute("backLink", "/human/work/role");
        model.addAttribute("detailedWorkLink", "/role/roleId=");
        return "/work/work_detail_list";
    }

    @GetMapping(value="/role/roleId={roleId}")
    public String roleInfo(@PathVariable("roleId")Long roleId, Model model, Principal principal) {
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = roleRepository.findByRoleId(roleId);
        Position position = positionRepository.findByPositionName("부장");

        Staff generalManager = staffRepository.findByRoleAndPosition(role, position);
        List<Staff> roleStaffs = staffRepository.findAllByRole(role);

        model.addAttribute("headerTitle", role.getRoleName());
        model.addAttribute("backLink", "/role/all");
        model.addAttribute("role", role);
        if(generalManager != null){
            model.addAttribute("generalManager", generalManager.getStaffName());
        }
        model.addAttribute("roleStaff", roleStaffs);
        model.addAttribute("updateLink", "/role/update/roleId=" + roleId);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("deleteLink", "/role/delete/roleId=" + roleId);
            model.addAttribute("isDeleteAuthority", true);
        }

        return "/work/human_resource/role";
    }
    @GetMapping(value="/role/insert")
    public String insertRolePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Position position = staff.getPosition();
        if(position.getPositionName().equals("사장") || position.getPositionName().equals("부사장") || position.getPositionName().equals("부장")){
            model.addAttribute("isInsertAuthority", true);
        }
        else{
            model.addAttribute("isInsertAuthority", false);
        }

        model.addAttribute("headerTitle", "직무 추가");
        model.addAttribute("link", "/role/insert");
        model.addAttribute("backLink", "/human/work/role");
        model.addAttribute("isNew", true);

        return "/work/human_resource/role";
    }
    @PostMapping(value="/role/insert")
    public String insertRole(RoleDto roleDto, RedirectAttributes attributes){
        if(roleDto.getRoleName().isEmpty()){
            attributes.addFlashAttribute("alertData", "직무 이름을 입력하세요.");
            return "redirect:/role/insert";
        }

        Role role = new Role();
        role.setRoleName(roleDto.getRoleName());
        roleRepository.save(role);
        attributes.addFlashAttribute("alertData", "직무가 생성되었습니다.");
        return "redirect:/human/work/role";
    }

    @GetMapping(value="/role/update")
    public String updateRolePage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff =staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Role> roles = roleRepository.findAll();
            LinkedHashMap<Long, String> role = new LinkedHashMap<>();
            for (Role r : roles) {
                role.put(r.getRoleId(), r.getRoleName());
            }

            model.addAttribute("headerTitle", "인사부 업무-직무관리-직무 개편");
            model.addAttribute("type", role);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/role");
            model.addAttribute("detailedWorkLink", "/role/roleId=");
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "수정 권한이 없습니다.");
            return "redirect:/human/work/role";
        }
    }

    @GetMapping(value="/role/update/roleId={roleId}")
    public String updateRoleInfoPage(@PathVariable("roleId") Long roleId, Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = roleRepository.findByRoleId(roleId);
        List<Staff> roleStaffs = staffRepository.findAllByRole(role);

        model.addAttribute("headerTitle", role.getRoleName());
        model.addAttribute("backLink", "/role/all");
        model.addAttribute("role", role);
        model.addAttribute("roleStaff", roleStaffs);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("isUpdateAuthority", true);
        }
        model.addAttribute("link", "/role/update/roleId=" + roleId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/role";
    }
    @PostMapping(value="/role/update/roleId={roleId}")
    public String updateRoleInfo(@PathVariable("roleId")Long roleId, RoleDto roleDto, RedirectAttributes attributes){
        Role role = roleRepository.findByRoleId(roleId);
        role.setRoleName(roleDto.getRoleName());
        roleRepository.save(role);
        attributes.addFlashAttribute("alertData", "직무 개편이 완료되었습니다.");
        return "redirect:/role/roleId=" + roleId;
    }

    @GetMapping(value="/role/delete")
    public String deleteRolePage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Role> roles = roleRepository.findAll();
            LinkedHashMap<Long, String> role = new LinkedHashMap<>();
            for (Role r : roles) {
                role.put(r.getRoleId(), r.getRoleName());
            }

            model.addAttribute("headerTitle", "인사부 업무-직무관리-직무 삭제");
            model.addAttribute("type", role);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/role");
            model.addAttribute("detailedWorkLink", "/role/roleId=");
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "삭제 권한이 없습니다.");
            return "redirect:/human/work/role";
        }
    }
    @GetMapping(value="/role/delete/roleId={roleId}")
    public String deleteRole(@PathVariable("roleId") Long roleId, RedirectAttributes attributes){
        Role role = roleRepository.findByRoleId(roleId);
        List<Staff> staffs = staffRepository.findAllByRole(role);
        if(staffs == null || staffs.isEmpty()){
            roleRepository.delete(role);
            attributes.addFlashAttribute("alertData", "직무가 삭제되었습니다.");
            return "redirect:/human/work/role";
        }
        else{
            attributes.addFlashAttribute("alertData", "직무 담당자들이 존재합니다.");
            return "redirect:/role/roleId=" + roleId;
        }
    }

    @GetMapping(value="/human/work/position")
    public String positionIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("직위 종류", "/position/all");
        works.put("직위 추가", "/position/insert");
        works.put("직위 개편", "/position/update");
        works.put("직위 삭제", "/position/delete");

        model.addAttribute("headerTitle", "인사부 업무-직위관리");
        model.addAttribute("works", works);
        model.addAttribute("backLink", "/human/work");

        return "/work/work_detail";
    }
    @GetMapping(value="/position/all")
    public String allPosition(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<Position> positions = positionRepository.findAll();
        positions.remove(0);
        LinkedHashMap<Long, String> position = new LinkedHashMap<>();
        for(Position p : positions){
            position.put(p.getPositionId(), p.getPositionName());
        }

        model.addAttribute("headerTitle", "인사부 업무-직위관리-직위 종류");
        model.addAttribute("type", position);
        model.addAttribute("isType", true);
        model.addAttribute("backLink", "/human/work/position");
        model.addAttribute("detailedWorkLink", "/position/positionId=");
        return "/work/work_detail_list";
    }

    @GetMapping(value="/position/positionId={positionId}")
    public String positionInfo(@PathVariable("positionId") Long positionId, Model model, Principal principal) {
        Staff staff = staffRepository.findByUserId(principal.getName());
        Position position = positionRepository.findByPositionId(positionId);

        List<Staff> positionStaffs = staffRepository.findAllByPosition(position);

        model.addAttribute("headerTitle", position.getPositionName());
        model.addAttribute("backLink", "/position/all");
        model.addAttribute("position", position);

        model.addAttribute("positionStaff", positionStaffs);
        model.addAttribute("updateLink", "/position/update/positionId=" + positionId);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("deleteLink", "/position/delete/positionId=" + positionId);
            model.addAttribute("isDeleteAuthority", true);
        }

        return "/work/human_resource/position";
    }
    @GetMapping(value="/position/insert")
    public String insertPositionPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = staff.getRole();

        if(role.getRoleName().equals("경영진")){
            model.addAttribute("isInsertAuthority", true);
        }
        else{
            model.addAttribute("isInsertAuthority", false);
        }

        model.addAttribute("headerTitle", "직위 추가");
        model.addAttribute("link", "/position/insert");
        model.addAttribute("backLink", "/human/work/position");
        model.addAttribute("isNew", true);

        return "/work/human_resource/position";
    }
    @PostMapping(value="/position/insert")
    public String insertPosition(PositionDto positionDto, RedirectAttributes attributes){
        if(positionDto.getPositionName().isEmpty()){
            attributes.addFlashAttribute("alertData", "직위 이름을 입력하세요.");
            return "redirect:/position/insert";
        }

        Position position = new Position();
        position.setPositionName(positionDto.getPositionName());
        positionRepository.save(position);

        attributes.addFlashAttribute("alertData", "직위가 생성되었습니다.");
        return "redirect:/human/work/position";
    }

    @GetMapping(value="/position/update")
    public String updatePositionPage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff =staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Position> positions = positionRepository.findAll();
            LinkedHashMap<Long, String> position = new LinkedHashMap<>();
            for (Position p : positions) {
                position.put(p.getPositionId(), p.getPositionName());
            }

            model.addAttribute("headerTitle", "인사부 업무-직위관리-직위 개편");
            model.addAttribute("type", position);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/position");
            model.addAttribute("detailedWorkLink", "/position/positionId=");
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "수정 권한이 없습니다.");
            return "redirect:/human/work/position";
        }
    }

    @GetMapping(value="/position/update/positionId={positionId}")
    public String updatePositionInfoPage(@PathVariable("positionId") Long positionId, Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Position position = positionRepository.findByPositionId(positionId);
        List<Staff> positionStaffs = staffRepository.findAllByPosition(position);

        model.addAttribute("headerTitle", position.getPositionName());
        model.addAttribute("backLink", "/position/all");
        model.addAttribute("position", position);
        model.addAttribute("positionStaff", positionStaffs);
        if(staff.getRole().getRoleName().equals("경영진")){
            model.addAttribute("isUpdateAuthority", true);
        }
        model.addAttribute("link", "/position/update/positionId=" + positionId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/position";
    }
    @PostMapping(value="/position/update/positionId={positionId}")
    public String updatePositionInfo(@PathVariable("positionId") Long positionId, PositionDto positionDto, RedirectAttributes attributes){
        Position position = positionRepository.findByPositionId(positionId);
        position.setPositionName(positionDto.getPositionName());
        positionRepository.save(position);
        attributes.addFlashAttribute("alertData", "직위 개편이 완료되었습니다.");
        return "redirect:/position/positionId=" + positionId;
    }

    @GetMapping(value="/position/delete")
    public String deletePositionPage(Model model, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        if(staff.getRole().getRoleName().equals("경영진")) {
            staffInfoService.getInfo(model, principal);

            List<Position> positions = positionRepository.findAll();
            LinkedHashMap<Long, String> position = new LinkedHashMap<>();
            for (Position p : positions) {
                position.put(p.getPositionId(), p.getPositionName());
            }

            model.addAttribute("headerTitle", "인사부 업무-직위관리-직위 삭제");
            model.addAttribute("type", position);
            model.addAttribute("isType", true);
            model.addAttribute("backLink", "/human/work/position");
            model.addAttribute("detailedWorkLink", "/position/positionId=");
            return "/work/work_detail_list";
        }
        else{
            attributes.addFlashAttribute("alertData", "삭제 권한이 없습니다.");
            return "redirect:/human/work/position";
        }
    }
    @GetMapping(value="/position/delete/positionId={positionId}")
    public String deletePosition(@PathVariable("positionId") Long positionId, RedirectAttributes attributes){
        Position position = positionRepository.findByPositionId(positionId);
        List<Staff> workInfo = staffRepository.findAllByPosition(position);
        if(workInfo == null || workInfo.isEmpty()){
            positionRepository.delete(position);
            attributes.addFlashAttribute("alertData", "직위가 삭제되었습니다.");
        }
        else {
            attributes.addFlashAttribute("alertData", "직위를 가진 사람들이 있습니다.");
        }
        return "redirect:/position/delete";
    }

    @GetMapping(value="/human/work/administrate")
    public String administrateIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("신규 직원 추가", "/signup");
        works.put("직원 조회/정보 변경", "/administrate/employee");

        model.addAttribute("headerTitle", "인사부 업무-인사행정");
        model.addAttribute("works", works);
        model.addAttribute("backLink", "/human/work");

        return "/work/work_detail";
    }

    @GetMapping(value="/administrate/employee")
    public String memberAdministrate(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Staff> paging = staffRepository.findAll(PageRequest.of(page, 10));

        model.addAttribute("headerTitle", "인사부 업무-인사행정-직원조회/정보변경");
        model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/administrate");
        model.addAttribute("detailedWorkLink", "/administrate/employee/employeeId=");
        model.addAttribute("isStaff", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/administrate/employee/employeeId={employeeId}")
    public String memberAdministrateDetail(@PathVariable("employeeId") Long employeeId, Model model){
        Staff staff = staffRepository.findByStaffId(employeeId);
        String headerTitle = "직원조회-" +  staff.getStaffName();
        model.addAttribute("headerTitle", headerTitle);
        model.addAttribute("staff", staff);
        model.addAttribute("link", "/administrate/employee/update/employeeId=" + employeeId);
        model.addAttribute("backLink", "/administrate/employee");
        return "/work/human_resource/administrate";
    }

    @GetMapping(value="/administrate/employee/update/employeeId={employeeId}")
    public String updateStaffInfoPage(@PathVariable("employeeId") Long employeeId, Model model){
        Staff staff = staffRepository.findByStaffId(employeeId);
        String headerTitle = "직원조회-" +  staff.getStaffName() +"-수정";
        model.addAttribute("headerTitle", headerTitle);
        model.addAttribute("staff", staff);
        model.addAttribute("link", "/administrate/employee/update/employeeId=" + employeeId);
        model.addAttribute("backLink", "/administrate/employee/employeeId=" + employeeId);
        model.addAttribute("isUpdate", true);
        return "/work/human_resource/administrate";
    }
    @PostMapping(value="/administrate/employee/update/employeeId={employeeId}")
    public String updateStaffInfo(@PathVariable("employeeId") Long employeeId, StaffDto staffDto, RedirectAttributes attributes){
        staffService.updateStaff(employeeId,  staffDto, true, null);
        attributes.addFlashAttribute("alertData", "직원 정보 수정이 완료되었습니다.");
        return "redirect:/administrate/employee/employeeId=" + employeeId;
    }

    @GetMapping(value="/human/work/holiday")
    public String holidayIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("휴가 종합", "/holiday/all");
        works.put("휴가 신청", "/holiday/requests");
        works.put("휴가 승인", "/holiday/approve");
        works.put("휴가 승인 보류", "/holiday/defer");
        works.put("휴가 반려", "/holiday/reject");

        model.addAttribute("headerTitle", "인사부 업무-휴가관리");
        model.addAttribute("works", works);
        model.addAttribute("backLink", "/human/work");

        return "/work/work_detail";
    }
    @GetMapping(value="/holiday/all")
    public String allResponseHolidays(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Page<Work> paging = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-휴가-휴가 신청");
        model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/holiday");
        model.addAttribute("detailedWorkLink", "/holiday/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/holiday/requests")
    public String requestHolidays(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        // Page<Work> paging = workRepository.findAllByWorkTypeAndDraftStateOrderByWorkIdDesc(PageRequest.of(page, 10), workType, DraftState.REQUEST);

        model.addAttribute("headerTitle", "인사부 업무-휴가-휴가 신청");
        // model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/holiday");
        model.addAttribute("detailedWorkLink", "/holiday/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/holiday/approve")
    public String allowHolidays(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        // Page<Work> paging = workRepository.findAllByWorkTypeAndDraftStateOrderByWorkIdDesc(PageRequest.of(page, 10), workType, DraftState.APPROVE);

        model.addAttribute("headerTitle", "인사부 업무-휴가-허가");
        // model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/holiday");
        model.addAttribute("detailedWorkLink", "/holiday/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/holiday/defer")
    public String delayHolidays(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        // Page<Work> paging = workRepository.findAllByWorkTypeAndDraftStateOrderByWorkIdDesc(PageRequest.of(page, 10), workType, DraftState.DEFER);

        model.addAttribute("headerTitle", "인사부 업무-휴가-보류");
        // model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/holiday");
        model.addAttribute("detailedWorkLink", "/holiday/response/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/holiday/reject")
    public String notAllowHolidays(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        // Page<Work> paging = workRepository.findAllByWorkTypeAndDraftStateOrderByWorkIdDesc(PageRequest.of(page, 10), workType, DraftState.REJECT);

        model.addAttribute("headerTitle", "인사부 업무-휴가-반려");
        // model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/holiday");
        model.addAttribute("detailedWorkLink", "/holiday/response/workId=");
        model.addAttribute("isWork", true);
        return "/work/work_detail_list";
    }

    @GetMapping(value="/holiday/workId={workId}")
    public String memberHolidayDetail(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);

        model.addAttribute("headerTitle", "인사부 업무-휴가-" + work.getDraftStaff().getStaffName());
        model.addAttribute("responseLink", "/holiday/consider/workId=" + workId);
        model.addAttribute("backLink", "/holiday/all");
        model.addAttribute("holiday", holiday);
        model.addAttribute("work", work);

        return "/work/human_resource/holiday";
    }

    @GetMapping(value="/holiday/consider/workId={workId}")
    public String considerMemberHolidayPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Staff responseStaff = staffRepository.findByUserId(principal.getName());
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);

        model.addAttribute("headerTitle", "인사부 업무-휴가-" + work.getDraftStaff().getStaffName());
        model.addAttribute("considerStaff", responseStaff);
        model.addAttribute("responseLink", "/holiday/response/consider/workId=" + workId);
        model.addAttribute("backLink", "/holiday/all");
        model.addAttribute("work", work);
        model.addAttribute("holiday", holiday);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/holiday";
    }

    @PostMapping(value="/holiday/consider/workId={workId}")
    public String considerMemberHoliday(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        if(!workDto.getDraftState().equals("NOT_SELECTED")){
            workRepository.save(work);

            attributes.addFlashAttribute("alertData", "처리가 완료되었습니다.");
            return "redirect:/holiday/workId=" + workId;
        }
        else{
            attributes.addFlashAttribute("alertData", "승인 여부를 정하세요.");
            return "redirect:/holiday/consider/workId=" + workId;
        }
    }

    @GetMapping(value="/human/work/commute")
    public String commuteIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("직원 근태 종합", "/commute/all");
        works.put("결근자 기록", "/commute/absence");
        works.put("일간 근태 결산", "/commute/daily");
        // works.put("주간 근태 결산", "/commute/weekly");
        works.put("월간 근태 결산", "/commute/monthly");

        model.addAttribute("headerTitle", "인사부 업무-근태관리");
        model.addAttribute("writeLink", "/human/work/commute/write");
        model.addAttribute("works", works);
        model.addAttribute("backLink", "/human/work");

        return "/work/work_detail";
    }
    @GetMapping(value="/commute/all")
    public String allCommute(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("근태기록");
        Page<Work> paging = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-직원 근태 종합");
        model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/human/work/commute");
        model.addAttribute("detailedWorkLink", "/commute/workId=");
        model.addAttribute("isWork", true);
        return "/work/work_detail_list";
    }

    /*
    @GetMapping(value="/commute/workId={workId}")
    public String allCommuteDaily(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Commute commute = commuteRepository.findByWork(work);
        LocalDate date = commute.getCommuteDate();

        model.addAttribute("headerTitle", "일간 근태 결산-" + work.getDraftStaff().getStaffName() + "-" + date);
        model.addAttribute("work", work);
        model.addAttribute("commute", commute);
        model.addAttribute("backLink", "/commute/daily");
        model.addAttribute("link", "/commute/daily/consider/workId=" + workId);
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/commute";
    }
    @GetMapping(value="/commute/absence")
    public String commuteAbsencePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-결근자 기록 작성");
        model.addAttribute("draftStaff", staff);
        model.addAttribute("backLink", "/human/work/commute");
        model.addAttribute("link", "/commute/absence");
        model.addAttribute("isNew", true);
        return "/work/human_resource/commute_absence";
    }
    @PostMapping(value="/commute/absence")
    public String searchCommuteAbsence(@RequestParam("staffName") String staffName, @RequestParam("userId") String userId,
                                       CommuteDto commuteDto, RedirectAttributes attributes){
        Staff absenceStaff = staffRepository.findByUserId(userId);

        if(absenceStaff.getStaffName().equals(staffName)){
            LocalDate absence = commuteDto.getAbsence();
            absence.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
            
            LocalDate requestDate = LocalDate.now();
            requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            LocalTime time = LocalTime.of(0, 0, 0);

            WorkType workType = workTypeRepository.findByWorkName("근태기록");

            Work work = new Work();
            work.setWorkType(workType);
            work.setDraftStaff(absenceStaff);
            work.setDraftState(WorkState.PENDING);
            workRepository.save(work);

            Commute commute = new Commute();
            commute.setStaff(absenceStaff);
            commute.setCommuteDate(absence);
            commute.setClockIn(time);
            commute.setClockOut(time);
            commute.setStatement(commuteDto.getCommuteStatement());
            commuteRepository.save(commute);

            attributes.addFlashAttribute("alertData", "결근자 기록이 완료되었습니다.");
            return "redirect:/human/work/commute";
        }
        else{
            attributes.addFlashAttribute("alertData", "결근자 정보를 다시 입력하세요");
            return "redirect:/commute/absence";
        }
    }
    @GetMapping(value="/commute/daily")
    public String allDetailedCommuteDaily(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("근태기록");
        Page<Work> commutes = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-일간 근태 결산");
        model.addAttribute("paging", commutes);
        model.addAttribute("backLink", "/human/work/commute");
        model.addAttribute("detailedWorkLink", "/commute/daily/workId=");
        model.addAttribute("isWork", true);
        return "/work/work_detail_list";
    }
    @GetMapping(value="/commute/daily/workId={workId}")
    public String detailedCommuteDailyPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Commute commute = commuteRepository.findByWork(work);

        model.addAttribute("headerTitle", "일간 근태 결산-" + work.getDraftStaff().getStaffName());
        model.addAttribute("work", work);
        model.addAttribute("commute", commute);
        model.addAttribute("backLink", "/commute/daily");
        model.addAttribute("link", "/commute/daily/consider/workId=" + workId);
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/commute";
    }
    @GetMapping(value="/commute/daily/consider/workId={workId}")
    public String considerCommuteDailyPage(@PathVariable("workId") Long workId, Model model, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Commute commute = commuteRepository.findByWork(work);

        Staff considerStaff = staffRepository.findByUserId(principal.getName());
        if(considerStaff.getUserId().equals(work.getDraftStaff().getUserId())){
            attributes.addFlashAttribute("alertData", "결재권한이 없습니다.");
            return "redirect:/commute/daily/workId=" + workId;
        }

        model.addAttribute("headerTitle", "일간 근태 결산-" + work.getDraftStaff().getStaffName() + "-결산");
        model.addAttribute("work", work);
        model.addAttribute("commute", commute);
        model.addAttribute("backLink", "/commute/daily");

        model.addAttribute("link", "/commute/daily/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/commute";
    }
    @PostMapping(value="/commute/daily/consider/workId={workId}")
    public String considerCommuteDaily(@PathVariable("workId") Long workId, WorkDto workDto, @RequestParam("commuteType") String commuteType,
                                       Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Commute commute = commuteRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        LocalDate responseDate = LocalDate.now();
        responseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        attributes.addFlashAttribute("alertData", "일간 근태 결산이 완료되었습니다.");
        return "redirect:/commute/daily/workId=" + workId;
    }
    @GetMapping(value="/commute/monthly")
    public String commuteMonthly(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("근태결산");
        Page<Work> commutes = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-일간 근태 결산");
        model.addAttribute("paging", commutes);
        model.addAttribute("backLink", "/human/work/commute");
        model.addAttribute("detailedWorkLink", "/commute/monthly/workId=");
        model.addAttribute("link", "/commute/monthly/closing");
        model.addAttribute("linkName", "월간 근태결산");
        model.addAttribute("isRequest", true);
        model.addAttribute("isWork", true);
        return "/work/work_detail_list";
    }
    @GetMapping(value="/commute/monthly/closing")
    public String commuteMonthlyClosingWrite(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "월간 근태 결산서 작성");
        model.addAttribute("backLink", "/commute/monthly");
        model.addAttribute("draftStaff", staff);
        model.addAttribute("isNew", true);
        return "/work/human_resource/commute_closing";
    }
    @PostMapping("/commute/monthly/closing")
    public String commuteMonthlyClosing(CommuteClosingDto commuteClosingDto, WorkDto workDto, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(commuteClosingDto.getUserId());
        String staffName = staff.getStaffName();
        if(staffName.equals(commuteClosingDto.getStaff())){
            Staff requestStaff = staffRepository.findByStaffName(workDto.getDraftStaff());
            CommuteMonthlyRecord commuteMonthlyRecord = new CommuteMonthlyRecord();
            WorkType workType = workTypeRepository.findByWorkName("근태결산");
            LocalDate requestDate = LocalDate.now();

            String [] monthlyDate = commuteClosingDto.getMonthly().split("-");
            String monthly;
            if(monthlyDate[1].length() == 1){
                monthly = monthlyDate[0] + "-0" + monthlyDate[1];
            }
            else{
                monthly = monthlyDate[0] + "-" + monthlyDate[1];
            }

            Work work = new Work();
            work.setWorkType(workType);
            work.setDraftStaff(staff);
            work.setDraftState(WorkState.PENDING);
            workRepository.save(work);

            commuteMonthlyRecord.setStaff(staff);
            commuteMonthlyRecord.setAttendance(commuteClosingDto.getAttendance());
            commuteMonthlyRecord.setAbsence(commuteClosingDto.getAbsence());
            commuteMonthlyRecord.setLateness(commuteClosingDto.getLateness());
            commuteMonthlyRecord.setHoliday(commuteClosingDto.getHoliday());
            commuteMonthlyRecord.setWork(work);
            commuteMonthlyRecordRepository.save(commuteMonthlyRecord);

            attributes.addFlashAttribute("alertData", "월간 근태결산이 작성되었습니다.");
            return "redirect:/commute/monthly";
        }
        else{
            attributes.addFlashAttribute("alertData", "직원명과 직원 아이디를 다시 입력하세요");
            return "redirect:/commute/monthly/closing";
        }
    }
    @GetMapping(value="/commute/monthly/workId={workId}")
    public String commuteMonthlyClosing(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        CommuteMonthlyRecord commuteMonthlyRecord = commuteMonthlyRecordRepository.findByWork(work);
        Staff commuteStaff = commuteMonthlyRecord.getStaff();
        Staff curStaff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-월간 근태 결산-" + commuteStaff.getStaffName());
        model.addAttribute("backLink", "/commute/monthly");
        if(curStaff.getUserId().equals(work.getDraftStaff().getUserId())){
            model.addAttribute("link", "/commute/monthly/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/commute/monthly/consider/workId=" + workId);
        }
        model.addAttribute("staff", commuteStaff);
        model.addAttribute("commuteClosing", commuteMonthlyRecord);
        model.addAttribute("work", work);


        return "/work/human_resource/commute_closing";
    }
    @GetMapping(value="/commute/monthly/update/workId={workId}")
    public String updateCommuteMonthlyClosingPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        CommuteMonthlyRecord commuteMonthlyRecord = commuteMonthlyRecordRepository.findByWork(work);
        Staff staff = commuteMonthlyRecord.getStaff();

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-월간 근태 결산-" + staff.getStaffName());
        model.addAttribute("backLink", "/commute/monthly/workId=" + workId);
        model.addAttribute("link", "/commute/monthly/update/workId=" + workId);
        model.addAttribute("staff", staff);
        model.addAttribute("commuteClosing", commuteMonthlyRecord);
        model.addAttribute("work", work);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/commute_closing";
    }
    @PostMapping(value="/commute/monthly/update/workId={workId}")
    public String updateCommuteMonthlyClosing(@PathVariable("workId") Long workId, CommuteClosingDto commuteClosingDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        CommuteMonthlyRecord commuteMonthlyRecord = commuteMonthlyRecordRepository.findByWork(work);

        commuteMonthlyRecord.setAttendance(commuteClosingDto.getAttendance());
        commuteMonthlyRecord.setAbsence(commuteClosingDto.getAbsence());
        commuteMonthlyRecord.setLateness(commuteClosingDto.getLateness());
        commuteMonthlyRecord.setHoliday(commuteClosingDto.getHoliday());
        commuteMonthlyRecordRepository.save(commuteMonthlyRecord);

        attributes.addFlashAttribute("alertData", "월간 근태 결산 내용이 수정되었습니다.");
        return "redirect:/commute/monthly/workId=" + workId;
    }
    @GetMapping(value="/commute/monthly/consider/workId={workId}")
    public String considerCommuteMonthlyClosingPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        CommuteMonthlyRecord commuteMonthlyRecord = commuteMonthlyRecordRepository.findByWork(work);
        Staff staff = commuteMonthlyRecord.getStaff();
        Staff responseStaff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "인사부 업무-근태 관리-월간 근태 결산-" + staff.getStaffName());
        model.addAttribute("backLink", "/commute/monthly/workId=" + workId);
        model.addAttribute("link", "/commute/monthly/consider/workId=" + workId);
        model.addAttribute("staff", staff);
        model.addAttribute("commuteClosing", commuteMonthlyRecord);
        model.addAttribute("work", work);
        model.addAttribute("considerStaff", responseStaff);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/commute_closing";
    }
    @PostMapping(value="/commute/monthly/consider/workId={workId}")
    public String considerCommuteMonthlyClosingPage(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();

        workRepository.save(work);

        attributes.addFlashAttribute("alertData", "월간 근태 결산이 완료되었습니다.");
        return "redirect:/commute/monthly/workId=" + workId;
    }
     */

    @GetMapping(value="/human/work/recruit")
    public String recruitIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("채용 공고", "/recruit/recruit_notice");
        works.put("지원자", "/recruit/applicant");
        // works.put("중간 선발 결과", "/recruit/temporary");
        works.put("선발 결과", "/recruit/qualify");

        model.addAttribute("headerTitle", "인사부 업무-채용관리");
        model.addAttribute("backLink", "/human/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/recruit/recruit_notice")
    public String recruitNotice(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("채용공고");
        Page<Work> notice = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-채용관리-채용공고");
        model.addAttribute("paging", notice);
        model.addAttribute("isWork", true);
        model.addAttribute("backLink", "/human/work/recruit");
        model.addAttribute("detailedWorkLink", "/recruit/recruit_notice/workId=");
        model.addAttribute("isRequest", true);
        model.addAttribute("link", "/recruit/recruit_notice/insert");
        model.addAttribute("linkName", "공고 작성");

        return "/work/work_detail_list";
    }

    @GetMapping(value="/recruit/recruit_notice/insert")
    public String insertRecruitNoticeIndex(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        model.addAttribute("headerTitle", "채용 공고 작성");
        model.addAttribute("backLink", "/recruit/recruit_notice");
        model.addAttribute("draftStaff", staff);
        model.addAttribute("isNew", true);
        return "/work/human_resource/recruit";
    }
    @PostMapping(value="/recruit/recruit_notice/insert")
    public String insertRecruitNotice(RecruitDto recruitDto, WorkDto workDto, RedirectAttributes attributes){
        Staff staff = staffRepository.findByStaffName((workDto.getDraftStaff()));
        WorkType workType = workTypeRepository.findByWorkName("채용공고");
        Position position = positionRepository.findByPositionName(recruitDto.getPosition());
        Role role = roleRepository.findByRoleName(recruitDto.getRole());

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Recruit recruit = new Recruit();
        String name = recruitDto.getRole() + "-" + recruitDto.getPosition() + "/" + recruitDto.getStartDate();
        recruit.setWork(work);
        recruit.setRecruitName(name);
        recruit.setPosition(position);
        recruit.setRole(role);
        recruit.setQualification(recruitDto.getQualification());
        recruit.setPreference(recruitDto.getPreference());
        recruit.setRecruitNumber(recruitDto.getRecruitNumber());
        recruit.setEmploymentStatus(recruitDto.getEmploymentStatus());
        recruit.setPay(recruitDto.getPay());
        recruit.setPlace(recruitDto.getPlace());
        recruit.setProcess(recruitDto.getProcess());
        recruit.setDocument(recruitDto.getDocument());
        recruit.setMeans(recruitDto.getMeans());
        recruit.setStartDate(recruitDto.getStartDate());
        recruit.setEndDate(recruitDto.getEndDate());
        recruitRepository.save(recruit);

        attributes.addFlashAttribute("alertData", "공고 작성이 완료되었습니다.");
        return "redirect:/recruit/recruit_notice";
    }

    @GetMapping(value="/recruit/recruit_notice/workId={workId}")
    public String recruitNoticePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Recruit recruit = recruitRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());
        String curStaff = staff.getUserId();
        String reqStaff = recruit.getWork().getDraftStaff().getUserId();

        model.addAttribute("work", work);
        model.addAttribute("recruit", recruit);
        model.addAttribute("headerTitle", "채용공고");
        model.addAttribute("backLink", "/recruit/recruit_notice");
        model.addAttribute("isUpdatable", true);
        if(curStaff.equals(reqStaff)){
            model.addAttribute("link", "/recruit/recruit_notice/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/recruit/recruit_notice/consider/workId=" + workId);
        }

        return "/work/human_resource/recruit";
    }
    @GetMapping(value="/recruit/recruit_notice/update/workId={workId}")
    public String updateRecruitNoticePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Recruit recruit = recruitRepository.findByWork(work);

        model.addAttribute("work", work);
        model.addAttribute("recruit", recruit);
        model.addAttribute("headerTitle", "채용공고-승인여부");
        model.addAttribute("backLink", ("/recruit/recruit_notice/workId=" + workId));
        model.addAttribute("isUpdate", true);
        model.addAttribute("link", ("/recruit/recruit_notice/update/workId=" + workId));

        return "/work/human_resource/recruit";
    }
    @PostMapping(value="/recruit/recruit_notice/update/workId={workId}")
    public String updateRecruitNotice(@PathVariable("workId") Long workId, RecruitDto recruitDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Recruit recruit = recruitRepository.findByWork(work);

        Position position = positionRepository.findByPositionName(recruitDto.getPosition());
        Role role = roleRepository.findByRoleName(recruitDto.getRole());

        recruit.setPosition(position);
        recruit.setRole(role);
        recruit.setQualification(recruitDto.getQualification());
        recruit.setPreference(recruitDto.getPreference());
        recruit.setRecruitNumber(recruitDto.getRecruitNumber());
        recruit.setEmploymentStatus(recruitDto.getEmploymentStatus());
        recruit.setPay(recruitDto.getPay());
        recruit.setPlace(recruitDto.getPlace());
        recruit.setProcess(recruitDto.getProcess());
        recruit.setDocument(recruitDto.getDocument());
        recruit.setMeans(recruitDto.getMeans());
        recruit.setStartDate(recruitDto.getStartDate());
        recruit.setEndDate(recruitDto.getEndDate());
        recruitRepository.save(recruit);

        attributes.addFlashAttribute("alertData", "공고 수정이 완료되었습니다.");
        return "redirect:/recruit/recruit_notice/workId=" + workId;
    }
    @GetMapping(value="/recruit/recruit_notice/consider/workId={workId}")
    public String considerRecruitNoticePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Recruit recruit = recruitRepository.findByWork(work);
        if(staffRepository.findByUserId(principal.getName()) != null){
            model.addAttribute("responseStaff", staffRepository.findByUserId(principal.getName()));
        }
        model.addAttribute("work", work);
        model.addAttribute("recruit", recruit);
        model.addAttribute("headerTitle", "채용공고-승인여부");
        model.addAttribute("backLink", ("/recruit/recruit_notice/workId=" + workId));
        model.addAttribute("isSecondLevelApproval", true);
        model.addAttribute("link", ("/recruit/recruit_notice/consider/workId=" + workId));

        return "/work/human_resource/recruit";
    }
    @PostMapping(value="/recruit/recruit_notice/consider/workId={workId}")
    public String considerRecruitNotice(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());

        workRepository.save(work);

        return "redirect:/recruit/recruit_notice/workId=" + workId;
    }

    @GetMapping(value="/recruit/applicant")
    public String recruitApplicant(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("지원자");
        Page<Work> applicants = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-채용관리-지원자");
        model.addAttribute("paging", applicants);
        model.addAttribute("detailedWorkLink", "/recruit/applicant/workId=");
        model.addAttribute("backLink", "/human/work/recruit");
        model.addAttribute("link", "/recruit/applicant/insert");
        model.addAttribute("linkName", "지원자 추가");
        model.addAttribute("isRequest", true);
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/recruit/applicant/insert")
    public String insertRecruitApplicantPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "지원자 추가");
        model.addAttribute("backLink", "/recruit/applicant");
        model.addAttribute("link", "/recruit/applicant/insert");
        model.addAttribute("isNew", true);

        return "work/human_resource/recruit_applicant";
    }
    @PostMapping(value="/recruit/applicant/insert")
    public String insertRecruitApplicant(ApplicantDto applicantDto, Principal principal, RedirectAttributes attributes){
        Work work = new Work();
        WorkType workType = workTypeRepository.findByWorkName("지원자");
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate reqDate = LocalDate.now();
        reqDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Recruit recruit = recruitRepository.findByRecruitName(applicantDto.getRecruit());
        if (recruit == null){
            attributes.addFlashAttribute("alertData", "채용 공고를 다시 입력하세요");
            return "redirect:/recruit/applicant/insert";
        }
        Role role = roleRepository.findByRoleName(applicantDto.getRole());
        if(!recruit.getRole().getRoleName().equals(role.getRoleName())){
            attributes.addFlashAttribute("alertData", "지원 분야를 다시 입력하세요");
            return "redirect:/recruit/applicant/insert";
        }
        if(!applicantDto.getGender().startsWith("남") || !applicantDto.getGender().startsWith("여")){
            attributes.addFlashAttribute("alertData", "성별을 다시 입력하세요");
            return "redirect:/recruit/applicant/insert";
        }

        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);
        
        Applicant applicant = new Applicant();
        applicant.setWork(work);
        applicant.setName(applicantDto.getName());
        applicant.setRecruit(recruit);
        applicant.setBirthday(applicantDto.getBirthday());
        applicant.setRole(role);
        applicant.setCareer(applicantDto.getCareer());
        applicant.setSpecification(applicantDto.getSpecification());
        String gender = applicantDto.getGender();
        if(gender.startsWith("남")){
            applicant.setGender(Gender.MALE);
        }
        else if(gender.startsWith("여")){
            applicant.setGender(Gender.FEMALE);
        }
        applicantRepository.save(applicant);

        attributes.addFlashAttribute("alertData", "지원자 등록이 완료되었습니다.");
        return "redirect:/recruit/applicant";
    }

    @GetMapping(value="/recruit/applicant/workId={workId}")
    public String recruitApplicantPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("work", work);
        model.addAttribute("applicant", applicant);
        model.addAttribute("headerTitle", "지원자");
        model.addAttribute("backLink", "/recruit/applicant");
        model.addAttribute("isUpdatable", true);
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/recruit/applicant/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/recruit/applicant/consider/workId=" + workId);
        }
        return "/work/human_resource/recruit_applicant";
    }
    @GetMapping(value="/recruit/applicant/update/workId={workId}")
    public String updateRecruitApplicantPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);

        model.addAttribute("headerTitle", "지원자 수정");
        model.addAttribute("work", work);
        model.addAttribute("applicant", applicant);
        model.addAttribute("backLink", "/recruit/applicant/workId=" + workId);
        model.addAttribute("link", "/recruit/applicant/update/workId="+workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/recruit_applicant";
    }

    @PostMapping(value="/recruit/applicant/update/workId={workId}")
    public String updateRecruitApplicant(@PathVariable("workId") Long workId, ApplicantDto applicantDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);
        Role role = roleRepository.findByRoleName(applicantDto.getRole());
        Recruit recruit = recruitRepository.findByRecruitName(applicantDto.getRecruit());

        applicant.setName(applicantDto.getName());
        applicant.setRecruit(recruit);
        applicant.setBirthday(applicantDto.getBirthday());
        if(applicantDto.getGender().startsWith("남")){
            applicant.setGender(Gender.MALE);
        }
        else{
            applicant.setGender(Gender.FEMALE);
        }
        applicant.setRole(role);
        applicant.setSpecification(applicantDto.getSpecification());
        applicant.setCareer(applicantDto.getCareer());
        applicantRepository.save(applicant);

        return "redirect:/recruit/applicant/workId=" + workId;
    }

    @GetMapping(value="/recruit/applicant/consider/workId={workId}")
    public String considerRecruitApplicant(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "지원자 선발");
        model.addAttribute("work", work);
        model.addAttribute("applicant", applicant);
        model.addAttribute("considerStaff", curStaff);
        model.addAttribute("backLink", "/recruit/applicant/workId=" + workId);
        model.addAttribute("link", "/recruit/applicant/consider/workId="+workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/recruit_applicant";
    }

    @PostMapping(value="/recruit/applicant/consider/workId={workId}")
    public String considerRecruitApplicant(@PathVariable("workId") Long workId, WorkDto workDto, ApplicantDto applicantDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("선발결과");

        workRepository.save(work);

        applicant.setQualify(applicantDto.getQualify());
        applicantRepository.save(applicant);

        attributes.addFlashAttribute("alertData", "지원자 선발 결과가 저장되었습니다.");
        return "redirect:/recruit/applicant/workId=" + workId;
    }

    @GetMapping(value="/recruit/qualify")
    public String AllQualifyApplicant(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("선발결과");
        Page<Work> applicants = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-채용관리-선발결과");
        model.addAttribute("paging", applicants);
        model.addAttribute("backLink", "/human/work/recruit");
        model.addAttribute("detailedWorkLink", "/recruit/qualify/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/recruit/qualify/workId={workId}")
    public String qualifyApplicant(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);

        model.addAttribute("work", work);
        model.addAttribute("applicant", applicant);
        model.addAttribute("headerTitle", "지원자 선발 결과");
        model.addAttribute("backLink", "/recruit/qualify");
        model.addAttribute("link", "/recruit/qualify/consider/workId=" + workId);
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/recruit_applicant";
    }

    @GetMapping(value="/recruit/qualify/consider/workId={workId}")
    public String considerQualifyApplicantPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Applicant applicant = applicantRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("applicant", applicant);
        model.addAttribute("headerTitle", "지원자 선발 결과");
        model.addAttribute("backLink", "/recruit/qualify");
        model.addAttribute("link", "/recruit/qualify/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/recruit_applicant";
    }
    @PostMapping(value="/recruit/qualify/consider/workId={workId}")
    public String considerQualifyApplicant(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate response = LocalDate.now();
        response.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/recruit/qualify/workId=" + workId;
    }

    @GetMapping(value="/human/work/training")
    public String trainingIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("교육 프로그램", "/training/program");
        works.put("교육 대상자/지원자", "/training/trainee");
        works.put("교육 평가", "/training/appraisal");

        model.addAttribute("headerTitle", "인사부 업무-교육관리");
        model.addAttribute("backLink", "/human/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/training/program")
    public String allTrainingProgram(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("교육 프로그램");
        Page<Work> programs = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-교육관리-교육 프로그램");
        model.addAttribute("paging", programs);
        model.addAttribute("link", "/training/program/insert");
        model.addAttribute("linkName", "교육 프로그램 추가");
        model.addAttribute("backLink", "/human/work/training");
        model.addAttribute("detailedWorkLink", "/training/program/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/training/program/insert")
    public String createTrainingProgramPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "교육 프로그램 추가");
        model.addAttribute("backLink", "/training/program");
        model.addAttribute("link", "/training/program/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/training";
    }

    @PostMapping(value="/training/program/insert")
    public String createTrainingProgram(TrainingDto trainingDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("교육 프로그램");

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);;

        Training training = new Training();
        training.setWork(work);
        training.setName(trainingDto.getName());
        training.setContent(trainingDto.getContent());
        training.setTrainingDay(trainingDto.getTrainingDay());
        training.setStartDate(trainingDto.getStartDate());
        training.setEndDate(trainingDto.getEndDate());
        training.setStatement(trainingDto.getStatement());
        trainingRepository.save(training);

        attributes.addFlashAttribute("alertData", "교육 프로그램을 추가하였습니다.");
        return "redirect:/training/program";
    }

    @GetMapping(value="/training/program/workId={workId}")
    public String trainingProgram(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Training training = trainingRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "교육 프로그램");
        model.addAttribute("work", work);
        model.addAttribute("training", training);
        model.addAttribute("backLink", "/training/program");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/training/program/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/training/program/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/training";
    }
    @GetMapping(value="/training/program/update/workId={workId}")
    public String updateTrainingProgramPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Training training = trainingRepository.findByWork(work);

        model.addAttribute("headerTitle", "교육 프로그램 수정");
        model.addAttribute("work", work);
        model.addAttribute("training", training);
        model.addAttribute("backLink", "/training/program/workId=" + workId);
        model.addAttribute("link", "/training/program/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/training";
    }
    @PostMapping(value="/training/program/update/workId={workId}")
    public String updateTrainingProgram(@PathVariable("workId") Long workId, TrainingDto trainingDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Training training = trainingRepository.findByWork(work);

        training.setName(trainingDto.getName());
        training.setContent(trainingDto.getContent());
        training.setTrainingDay(trainingDto.getTrainingDay());
        training.setStatement(trainingDto.getStatement());
        training.setStartDate(trainingDto.getStartDate());
        training.setEndDate((trainingDto.getEndDate()));
        trainingRepository.save(training);

        attributes.addFlashAttribute("alertData", "교육 프로그램을 수정하였습니다.");
        return "redirect:/training/program/workId=" + workId;
    }

    @GetMapping(value="/training/program/consider/workId={workId}")
    public String considerTrainingProgramPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Training training = trainingRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("training", training);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/training/program/workId=" + workId);
        model.addAttribute("link", "/training/program/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/training";
    }

    @PostMapping(value="/training/program/consider/workId={workId}")
    public String considerTrainingProgram(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/training/program/workId=" + workId;
    }
    
    @GetMapping(value="/training/trainee")
    public String allTrainee(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("교육 대상자");
        Page<Work> trainee = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-교육 관리-교육 대상자");
        model.addAttribute("paging", trainee);
        model.addAttribute("link", "/training/trainee/insert");
        model.addAttribute("linkName", "교육 대상자 추가");
        model.addAttribute("backLink", "/human/work/training");
        model.addAttribute("detailedWorkLink", "/training/trainee/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/training/trainee/insert")
    public String createTraineePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "교육 프로그램 추가");
        model.addAttribute("backLink", "/training/trainee");
        model.addAttribute("link", "/training/trainee/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/trainee";
    }

    @PostMapping(value="/training/trainee/insert")
    public String createTrainee(TraineeDto traineeDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Staff traineeStaff = staffRepository.findByUserId(traineeDto.getUserId());
        Training training = trainingRepository.findByName(traineeDto.getTraining());
        WorkType workType = workTypeRepository.findByWorkName("교육 대상자");

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Trainee trainee = new Trainee();
        trainee.setWork(work);
        if(traineeStaff.getUserId().equals(staffRepository.findByStaffName(traineeDto.getStaff()).getUserId())){
            trainee.setStaff(traineeStaff);
        }
        else{
            attributes.addFlashAttribute("alertData", "교육 대상자가 아닙니다.");
            return "redirect:/training/trainee/insert";
        }
        trainee.setTraining(training);
        traineeRepository.save(trainee);

        attributes.addFlashAttribute("alertData", "교육 프로그램을 추가하였습니다.");
        return "redirect:/training/trainee";
    }

    @GetMapping(value="/training/trainee/workId={workId}")
    public String trainee(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Trainee trainee = traineeRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "교육 대상자");
        model.addAttribute("work", work);
        model.addAttribute("trainee", trainee);
        model.addAttribute("backLink", "/training/trainee");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/training/trainee/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/training/trainee/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/trainee";
    }
    @GetMapping(value="/training/trainee/update/workId={workId}")
    public String updateTraineePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Trainee trainee = traineeRepository.findByWork(work);

        model.addAttribute("headerTitle", "교육 대상자 수정");
        model.addAttribute("work", work);
        model.addAttribute("trainee", trainee);
        model.addAttribute("backLink", "/training/trainee/workId=" + workId);
        model.addAttribute("link", "/training/trainee/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/trainee";
    }
    @PostMapping(value="/training/trainee/update/workId={workId}")
    public String updateTrainee(@PathVariable("workId") Long workId, TraineeDto traineeDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Trainee trainee = traineeRepository.findByWork(work);
        Training training = trainingRepository.findByName(traineeDto.getTraining());

        trainee.setTraining(training);
        traineeRepository.save(trainee);

        attributes.addFlashAttribute("alertData", "교육 프로그램을 수정하였습니다.");
        return "redirect:/training/trainee/workId=" + workId;
    }

    @GetMapping(value="/training/trainee/consider/workId={workId}")
    public String considerTraineePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Trainee trainee = traineeRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("trainee", trainee);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/training/program/workId=" + workId);
        model.addAttribute("link", "/training/program/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/trainee";
    }

    @PostMapping(value="/training/trainee/consider/workId={workId}")
    public String considerTrainee(@PathVariable("workId") Long workId, WorkDto workDto, TraineeDto traineeDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Trainee trainee = traineeRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        trainee.setFeedback(traineeDto.getFeedback());
        traineeRepository.save(trainee);

        return "redirect:/training/trainee/workId=" + workId;
    }

    @GetMapping(value="/training/appraisal")
    public String allTrainingAppraisal(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("교육 평가");
        Page<Work> appraisal = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-교육 관리-교육 평가");
        model.addAttribute("paging", appraisal);
        model.addAttribute("link", "/training/appraisal/insert");
        model.addAttribute("linkName", "교육 평가");
        model.addAttribute("backLink", "/human/work/training");
        model.addAttribute("detailedWorkLink", "/training/appraisal/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/training/appraisal/insert")
    public String createTrainingAppraisalPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "교육 평가 추가");
        model.addAttribute("backLink", "/training/appraisal");
        model.addAttribute("link", "/training/appraisal/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/training_appraisal";
    }

    @PostMapping(value="/training/appraisal/insert")
    public String createTrainingAppraisal(TrainingAppraisalDto trainingAppraisalDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Staff traineeStaff = staffRepository.findByUserId(trainingAppraisalDto.getUserId());

        Trainee trainee = traineeRepository.findByStaff(traineeStaff);
        Training training = trainingRepository.findByName(trainingAppraisalDto.getTraining());

        WorkType workType = workTypeRepository.findByWorkName("교육 평가");


        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        TrainingAppraisal trainingAppraisal = new TrainingAppraisal();
        trainingAppraisal.setTrainee(trainee);
        trainingAppraisal.setTraining(training);
        trainingAppraisal.setWork(work);
        trainingAppraisal.setGrade(trainingAppraisalDto.getGrade());
        trainingAppraisal.setAppraisal(trainingAppraisalDto.getAppraisal());
        trainingAppraisalRepository.save(trainingAppraisal);

        attributes.addFlashAttribute("alertData", "교육 평가를 추가하였습니다.");
        return "redirect:/training/appraisal";
    }

    @GetMapping(value="/training/appraisal/workId={workId}")
    public String trainingAppraisal(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        TrainingAppraisal trainingAppraisal = trainingAppraisalRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "교육 대상자");
        model.addAttribute("work", work);
        model.addAttribute("trainingAppraisal", trainingAppraisal);
        model.addAttribute("backLink", "/training/trainee");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/training/appraisal/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/training/appraisal/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/training_appraisal";
    }
    @GetMapping(value="/training/appraisal/update/workId={workId}")
    public String updateTrainingAppraisalPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        TrainingAppraisal trainingAppraisal = trainingAppraisalRepository.findByWork(work);

        model.addAttribute("headerTitle", "교육 평가 수정");
        model.addAttribute("work", work);
        model.addAttribute("trainingAppraisal", trainingAppraisal);
        model.addAttribute("backLink", "/training/appraisal/workId=" + workId);
        model.addAttribute("link", "/training/appraisal/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/training_appraisal";
    }
    @PostMapping(value="/training/appraisal/update/workId={workId}")
    public String updateTrainingAppraisal(@PathVariable("workId") Long workId, TrainingAppraisalDto trainingAppraisalDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        TrainingAppraisal trainingAppraisal = trainingAppraisalRepository.findByWork(work);
        Training training = trainingRepository.findByName(trainingAppraisalDto.getTraining());

        trainingAppraisal.setTraining(training);
        trainingAppraisal.setAppraisal(trainingAppraisalDto.getAppraisal());
        trainingAppraisal.setGrade(trainingAppraisalDto.getGrade());
        trainingAppraisalRepository.save(trainingAppraisal);

        attributes.addFlashAttribute("alertData", "교육 프로그램을 수정하였습니다.");
        return "redirect:/training/appraisal/workId=" + workId;
    }

    @GetMapping(value="/training/appraisal/consider/workId={workId}")
    public String considerTrainingAppraisalPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        TrainingAppraisal trainingAppraisal = trainingAppraisalRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("trainingAppraisal", trainingAppraisal);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/training/appraisal/workId=" + workId);
        model.addAttribute("link", "/training/appraisal/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/training_appraisal";
    }

    @PostMapping(value="/training/appraisal/consider/workId={workId}")
    public String considerTrainingAppraisal(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/training/appraisal/workId=" + workId;
    }

    @GetMapping(value="/human/work/appraisal")
    public String appraisalIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        // works.put("평가 대상자", "/appraisal/appraisee");
        works.put("인사고과", "/appraisal/result");
        works.put("실적", "/appraisal/performance");
        works.put("승진&직무변경", "/appraisal/change");

        model.addAttribute("headerTitle", "인사부 업무-평가관리");
        model.addAttribute("backLink", "/human/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/appraisal/result")
    public String allAppraisalResult(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("평가관리");
        Page<Work> appraisal = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-평가관리-인사고과");
        model.addAttribute("paging", appraisal);
        model.addAttribute("link", "/appraisal/result/insert");
        model.addAttribute("linkName", "인사고과");
        model.addAttribute("backLink", "/human/work/appraisal");
        model.addAttribute("detailedWorkLink", "/appraisal/result/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/appraisal/result/insert")
    public String createAppraisalResultPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "인사고과 추가");
        model.addAttribute("backLink", "/appraisal/result");
        model.addAttribute("link", "/appraisal/result/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/appraisal";
    }

    @PostMapping(value="/appraisal/result/insert")
    public String createAppraisalResult(AppraisalDto appraisalDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Staff appraisee = staffRepository.findByUserId(appraisalDto.getUserId());
        WorkType workType = workTypeRepository.findByWorkName("평가관리");

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Appraisal appraisal = new Appraisal();
        appraisal.setWork(work);
        appraisal.setStaff(appraisee);
        appraisal.setResult(appraisalDto.getResult());
        appraisal.setDate(appraisalDto.getDate());
        appraisal.setContribution(appraisalDto.getContribution());
        appraisal.setJudge(appraisalDto.getJudge());
        appraisal.setImprovement(appraisalDto.getImprovement());
        appraisal.setPropulsion(appraisalDto.getPropulsion());
        appraisal.setResolution(appraisalDto.getResolution());
        appraisal.setAccuracy(appraisalDto.getAccuracy());
        appraisal.setManagement(appraisalDto.getManagement());
        appraisal.setSincerity(appraisalDto.getSincerity());
        appraisal.setSympathy(appraisalDto.getSympathy());
        appraisal.setBelonging(appraisalDto.getBelonging());
        appraisal.setStatement(appraisalDto.getStatement());
        appraisalRepository.save(appraisal);

        attributes.addFlashAttribute("alertData", "인사고과를 추가하였습니다.");
        return "redirect:/appraisal/result";
    }

    @GetMapping(value="/appraisal/result/workId={workId}")
    public String appraisalResult(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Appraisal appraisal = appraisalRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "인사고과");
        model.addAttribute("work", work);
        model.addAttribute("appraisal", appraisal);
        model.addAttribute("backLink", "/appraisal/result");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/appraisal/result/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/appraisal/result/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/appraisal";
    }
    @GetMapping(value="/appraisal/result/update/workId={workId}")
    public String updateAppraisalResultPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Appraisal appraisal = appraisalRepository.findByWork(work);

        model.addAttribute("headerTitle", "인사고과 수정");
        model.addAttribute("work", work);
        model.addAttribute("appraisal", appraisal);
        model.addAttribute("backLink", "/appraisal/result/workId=" + workId);
        model.addAttribute("link", "/appraisal/result/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/appraisal";
    }
    @PostMapping(value="/appraisal/result/update/workId={workId}")
    public String updateAppraisalResult(@PathVariable("workId") Long workId, AppraisalDto appraisalDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Appraisal appraisal = appraisalRepository.findByWork(work);

        appraisal.setResult(appraisalDto.getResult());
        appraisal.setDate(appraisalDto.getDate());
        appraisal.setContribution(appraisalDto.getContribution());
        appraisal.setJudge(appraisalDto.getJudge());
        appraisal.setImprovement(appraisalDto.getImprovement());
        appraisal.setPropulsion(appraisalDto.getPropulsion());
        appraisal.setResolution(appraisalDto.getResolution());
        appraisal.setAccuracy(appraisalDto.getAccuracy());
        appraisal.setManagement(appraisalDto.getManagement());
        appraisal.setSincerity(appraisalDto.getSincerity());
        appraisal.setSympathy(appraisalDto.getSympathy());
        appraisal.setBelonging(appraisalDto.getBelonging());
        appraisal.setStatement(appraisalDto.getStatement());
        appraisalRepository.save(appraisal);

        attributes.addFlashAttribute("alertData", "인사고과를 수정하였습니다.");
        return "redirect:/appraisal/result/workId=" + workId;
    }

    @GetMapping(value="/appraisal/result/consider/workId={workId}")
    public String considerAppraisalResultPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Appraisal appraisal = appraisalRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("appraisal", appraisal);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/appraisal/result/workId=" + workId);
        model.addAttribute("link", "/appraisal/result/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/appraisal";
    }

    @PostMapping(value="/appraisal/result/consider/workId={workId}")
    public String considerAppraisalResult(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/appraisal/result/workId=" + workId;
    }

    @GetMapping(value="/appraisal/performance")
    public String allPerformance(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("실적");
        Page<Work> performance = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-평가관리-실적");
        model.addAttribute("paging", performance);
        model.addAttribute("link", "/appraisal/performance/insert");
        model.addAttribute("linkName", "실적");
        model.addAttribute("backLink", "/human/work/appraisal");
        model.addAttribute("detailedWorkLink", "/appraisal/performance/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/appraisal/performance/insert")
    public String createPerformancePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "실적 추가");
        model.addAttribute("backLink", "/appraisal/performance");
        model.addAttribute("link", "/appraisal/performance/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/performance";
    }

    @PostMapping(value="/appraisal/performance/insert")
    public String createPerformance(PerformanceDto performanceDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Staff appraisee = staffRepository.findByUserId(performanceDto.getUserId());
        WorkType workType = workTypeRepository.findByWorkName("실적");
        WorkType performanceWorkType = workTypeRepository.findByWorkName(performanceDto.getWorkType());

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Department department = appraisee.getDepartment();
        Section section = appraisee.getSection();

        Performance performance = new Performance();
        performance.setWork(work);
        performance.setWorkType(performanceWorkType);
        performance.setStaff(appraisee);
        performance.setDepartment(department);
        performance.setSection(section);
        performance.setName(performanceDto.getName());
        performance.setPlan(performanceDto.getPlan());
        performance.setArchive(performanceDto.getArchive());
        performance.setRatio(performanceDto.getRatio());
        performance.setPerformanceStatement(performanceDto.getPerformanceStatement());
        performanceRepository.save(performance);

        attributes.addFlashAttribute("alertData", "실적을 추가하였습니다.");
        return "redirect:/appraisal/performance";
    }

    @GetMapping(value="/appraisal/performance/workId={workId}")
    public String performance(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Performance performance = performanceRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "실적");
        model.addAttribute("work", work);
        model.addAttribute("performance", performance);
        model.addAttribute("backLink", "/appraisal/performance");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/appraisal/performance/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/appraisal/performance/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/performance";
    }
    @GetMapping(value="/appraisal/performance/update/workId={workId}")
    public String updatePerformancePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Performance performance = performanceRepository.findByWork(work);

        model.addAttribute("headerTitle", "실적 수정");
        model.addAttribute("work", work);
        model.addAttribute("performance", performance);
        model.addAttribute("backLink", "/appraisal/performance/workId=" + workId);
        model.addAttribute("link", "/appraisal/performance/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/performance";
    }
    @PostMapping(value="/appraisal/performance/update/workId={workId}")
    public String updatePerformance(@PathVariable("workId") Long workId, PerformanceDto performanceDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        WorkType performanceWorkType = workTypeRepository.findByWorkName(performanceDto.getWorkType());

        Performance performance = performanceRepository.findByWork(work);
        performance.setWorkType(performanceWorkType);
        performance.setName(performanceDto.getName());
        performance.setPlan(performanceDto.getPlan());
        performance.setArchive(performanceDto.getArchive());
        performance.setRatio(performanceDto.getRatio());
        performance.setPerformanceStatement(performanceDto.getPerformanceStatement());
        performanceRepository.save(performance);

        attributes.addFlashAttribute("alertData", "실적 수정하였습니다.");
        return "redirect:/appraisal/performance/workId=" + workId;
    }

    @GetMapping(value="/appraisal/performance/consider/workId={workId}")
    public String considerPerformancePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Appraisal appraisal = appraisalRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("appraisal", appraisal);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/appraisal/performance/workId=" + workId);
        model.addAttribute("link", "/appraisal/performance/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/performance";
    }

    @PostMapping(value="/appraisal/performance/consider/workId={workId}")
    public String considerPerformance(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/appraisal/performance/workId=" + workId;
    }
    
    @GetMapping(value="/appraisal/change")
    public String allChange(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("승진&직무이동");
        Page<Work> performance = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-평가관리-승진&직무이동");
        model.addAttribute("paging", performance);
        model.addAttribute("link", "/appraisal/change/insert");
        model.addAttribute("linkName", "승진&직무이동");
        model.addAttribute("backLink", "/human/work/appraisal");
        model.addAttribute("detailedWorkLink", "/appraisal/change/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/appraisal/change/insert")
    public String createChangePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "승진&직무이동");
        model.addAttribute("backLink", "/appraisal/change");
        model.addAttribute("link", "/appraisal/change/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/promotion_change";
    }

    @PostMapping(value="/appraisal/change/insert")
    public String createChange(PromotionChangeDto promotionChangeDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("승진&직무이동");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Staff changedStaff = staffRepository.findByUserId(promotionChangeDto.getUserId());
        String position = changedStaff.getPosition().getPositionName();
        String role = changedStaff.getRole().getRoleName();

        PromotionChange promotionChange = new PromotionChange();
        promotionChange.setWork(work);
        promotionChange.setStaff(changedStaff);
        promotionChange.setCurrentPosition(position);
        promotionChange.setCurrentRole(role);
        promotionChange.setNextPosition(promotionChangeDto.getNextPosition());
        promotionChange.setNextRole(promotionChangeDto.getNextRole());
        promotionChange.setReason(promotionChangeDto.getReason());
        promotionChange.setDate(promotionChangeDto.getDate());
        promotionChangeRepository.save(promotionChange);

        attributes.addFlashAttribute("alertData", "승진&직무이동이 완료되였습니다.");
        return "redirect:/appraisal/change";
    }

    @GetMapping(value="/appraisal/change/workId={workId}")
    public String change(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        PromotionChange promotionChange = promotionChangeRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "승진&직무이동");
        model.addAttribute("work", work);
        model.addAttribute("promotionChange", promotionChange);
        model.addAttribute("backLink", "/appraisal/change");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/appraisal/change/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/appraisal/change/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/promotion_change";
    }
    @GetMapping(value="/appraisal/change/update/workId={workId}")
    public String updateChangePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        PromotionChange promotionChange = promotionChangeRepository.findByWork(work);

        model.addAttribute("headerTitle", "승진&직무이동 수정");
        model.addAttribute("work", work);
        model.addAttribute("promotionChange", promotionChange);
        model.addAttribute("backLink", "/appraisal/change/workId=" + workId);
        model.addAttribute("link", "/appraisal/change/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/promotion_change";
    }
    @PostMapping(value="/appraisal/change/update/workId={workId}")
    public String updateChange(@PathVariable("workId") Long workId, PromotionChangeDto promotionChangeDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        PromotionChange promotionChange = promotionChangeRepository.findByWork(work);

        promotionChange.setNextRole(promotionChangeDto.getNextRole());
        promotionChange.setNextPosition(promotionChangeDto.getNextPosition());
        promotionChange.setReason(promotionChangeDto.getReason());
        promotionChange.setDate(promotionChangeDto.getDate());
        promotionChangeRepository.save(promotionChange);

        attributes.addFlashAttribute("alertData", "승진&직무이동을 변경하였습니다.");
        return "redirect:/appraisal/change/workId=" + workId;
    }

    @GetMapping(value="/appraisal/change/consider/workId={workId}")
    public String considerChangePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        PromotionChange promotionChange = promotionChangeRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("promotionChange", promotionChange);
        model.addAttribute("rconsiderStaff", staff);
        model.addAttribute("backLink", "/appraisal/change/workId=" + workId);
        model.addAttribute("link", "/appraisal/change/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/promotion_change";
    }

    @PostMapping(value="/appraisal/change/consider/workId={workId}")
    public String considerChange(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/appraisal/change/workId=" + workId;
    }

    @GetMapping(value="/human/work/pay")
    public String payIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("급여 정보", "/pay/information");
        works.put("월간 급여 결산", "/pay/calculate");
        works.put("급여 지급", "/pay/payment");
        works.put("급여 명세서",  "/pay/payroll");

        model.addAttribute("headerTitle", "인사부 업무-급여 관리");
        model.addAttribute("backLink", "/human/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/pay/information")
    public String payInfoIndex(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("급여 정보");
        Page<Work> payInfo = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-급여 관리-급여정보");
        model.addAttribute("paging", payInfo);
        model.addAttribute("link", "/pay/information/insert");
        model.addAttribute("linkName", "급여 정보 추가");
        model.addAttribute("backLink", "/human/work/pay");
        model.addAttribute("detailedWorkLink", "/pay/information/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/pay/information/insert")
    public String createPayInfoPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "급여 정보 추가");
        model.addAttribute("backLink", "/pay/information");
        model.addAttribute("link", "/pay/information/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/pay_info";
    }

    @PostMapping(value="/pay/information/insert")
    public String createPayInfo(PayInfoDto payInfoDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        Role role = roleRepository.findByRoleName(payInfoDto.getRole());
        Position position = positionRepository.findByPositionName(payInfoDto.getPosition());
        WorkType workType = workTypeRepository.findByWorkName("급여 정보");

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        PayInfo payInfo = new PayInfo();
        payInfo.setWork(work);
        payInfo.setRole(role);
        payInfo.setPosition(position);
        payInfo.setBase(payInfoDto.getBase());
        payInfo.setOvertime(payInfoDto.getOvertime());
        payInfo.setNight(payInfoDto.getNight());
        payInfo.setWeekend(payInfoDto.getWeekend());
        payInfo.setHoliday(payInfoDto.getHoliday());
        payInfo.setYear(payInfoDto.getYear());
        payInfoRepository.save(payInfo);

        attributes.addFlashAttribute("alertData", "급여 정보를 추가하였습니다.");
        return "redirect:/pay/information";
    }

    @GetMapping(value="/pay/information/workId={workId}")
    public String payInfo(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        PayInfo payInfo = payInfoRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "급여 정보");
        model.addAttribute("work", work);
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("backLink", "/pay/information");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/pay/information/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/pay/information/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/pay_info";
    }
    @GetMapping(value="/pay/information/update/workId={workId}")
    public String updatePayInfoPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        PayInfo payInfo = payInfoRepository.findByWork(work);

        model.addAttribute("headerTitle", "급여 정보 수정");
        model.addAttribute("work", work);
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("backLink", "/pay/information/workId=" + workId);
        model.addAttribute("link", "/pay/information/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/pay_info";
    }
    @PostMapping(value="/pay/information/update/workId={workId}")
    public String updatePayInfo(@PathVariable("workId") Long workId, PayInfoDto payInfoDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        PayInfo payInfo = payInfoRepository.findByWork(work);

        payInfo.setBase(payInfoDto.getBase());
        payInfo.setOvertime(payInfoDto.getOvertime());
        payInfo.setNight(payInfoDto.getNight());
        payInfo.setWeekend(payInfoDto.getWeekend());
        payInfo.setHoliday(payInfoDto.getHoliday());
        payInfo.setYear(payInfoDto.getYear());
        payInfoRepository.save(payInfo);

        attributes.addFlashAttribute("alertData", "급여 정보를 수정하였습니다.");
        return "redirect:/pay/information/workId=" + workId;
    }

    @GetMapping(value="/pay/information/consider/workId={workId}")
    public String considerPayInfoPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        PayInfo payInfo = payInfoRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("payInfo", payInfo);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/pay/information/workId=" + workId);
        model.addAttribute("link", "/pay/information/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/pay_info";
    }

    @PostMapping(value="/pay/information/consider/workId={workId}")
    public String considerPayInfo(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/pay/information/workId=" + workId;
    }

    @GetMapping(value="/pay/calculate")
    public String payCalculateIndex(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("급여 관리");
        Page<Work> payInfo = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-급여 관리-급여 계산");
        model.addAttribute("paging", payInfo);
        model.addAttribute("link", "/pay/calculate/insert");
        model.addAttribute("linkName", "급여 계산");
        model.addAttribute("backLink", "/human/work/pay");
        model.addAttribute("detailedWorkLink", "/pay/calculate/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/pay/calculate/insert")
    public String createPayCalculatePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "급여 계산");
        model.addAttribute("backLink", "/pay/calculate");
        model.addAttribute("link", "/pay/calculate/insert");
        model.addAttribute("isNew", true);

        return "/work/human_resource/pay";
    }

    @PostMapping(value="/pay/calculate/insert")
    public String createPayCalculate(PayDto payDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("급여 관리");

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Staff paid = staffRepository.findByUserId(payDto.getUserId());
        Role role = paid.getRole();
        Position position = paid.getPosition();
        PayInfo payInfo = payInfoRepository.findByRoleAndPosition(role, position);

        Pay pay = new Pay();
        pay.setWork(work);
        pay.setStaff(paid);
        pay.setPayInfo(payInfo);
        pay.setOvertime(payDto.getOvertime());
        pay.setNight(payDto.getNight());
        pay.setWeekend(payDto.getWeekend());
        pay.setHoliday(payDto.getHoliday());
        pay.setPeriod(payDto.getPeriod());
        pay.setAdditionalHoliday((pay.getHoliday() * payInfo.getHoliday()));
        pay.setAdditionalWeekend((pay.getWeekend() * payInfo.getWeekend()));
        pay.setAdditionalOvertime((pay.getOvertime() * payInfo.getOvertime()));
        pay.setAdditionalNight((pay.getNight() * payInfo.getNight()));
        long total = pay.getAdditionalNight() + pay.getAdditionalOvertime() + pay.getAdditionalWeekend() + pay.getAdditionalHoliday();
        pay.setTotalAdditional(total);
        payRepository.save(pay);

        attributes.addFlashAttribute("alertData", "급여 계산이 완료되였습니다.");
        return "redirect:/pay/calculate";
    }

    @GetMapping(value="/pay/calculate/workId={workId}")
    public String payCalculate(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Pay pay = payRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "급여 정보");
        model.addAttribute("work", work);
        model.addAttribute("pay", pay);
        model.addAttribute("backLink", "/pay/calculate");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/pay/calculate/update/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/pay";
    }
    @GetMapping(value="/pay/calculate/update/workId={workId}")
    public String updatePayCalculatePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Pay pay = payRepository.findByWork(work);

        model.addAttribute("headerTitle", "급여 정보 수정");
        model.addAttribute("work", work);
        model.addAttribute("pay", pay);
        model.addAttribute("backLink", "/pay/calculate/workId=" + workId);
        model.addAttribute("link", "/pay/calculate/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/pay";
    }
    @PostMapping(value="/pay/calculate/update/workId={workId}")
    public String updatePayCalculate(@PathVariable("workId") Long workId, PayDto payDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Pay pay = payRepository.findByWork(work);
        PayInfo payInfo = pay.getPayInfo();

        pay.setOvertime(payDto.getOvertime());
        pay.setNight(payDto.getNight());
        pay.setWeekend(payDto.getWeekend());
        pay.setHoliday(payDto.getHoliday());
        pay.setPeriod(payDto.getPeriod());
        pay.setAdditionalHoliday((pay.getHoliday() * payInfo.getHoliday()));
        pay.setAdditionalWeekend((pay.getWeekend() * payInfo.getWeekend()));
        pay.setAdditionalOvertime((pay.getOvertime() * payInfo.getOvertime()));
        pay.setAdditionalNight((pay.getNight() * payInfo.getNight()));
        pay.setTotalAdditional((pay.getAdditionalNight() + pay.getAdditionalOvertime() + pay.getAdditionalWeekend() + pay.getAdditionalHoliday()));
        payRepository.save(pay);

        attributes.addFlashAttribute("alertData", "급여 계산 정보를 수정하였습니다.");
        return "redirect:/pay/calculate/workId=" + workId;
    }

    @GetMapping(value="/pay/payment")
    public String payIndex(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("급여 관리");
        Page<Work> pay = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "인사부 업무-급여 관리-급여 지급");
        model.addAttribute("paging", pay);
        model.addAttribute("backLink", "/human/work/pay");
        model.addAttribute("detailedWorkLink", "/pay/payment/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/pay/payment/workId={workId}")
    public String pay(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Pay pay = payRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "급여 지급");
        model.addAttribute("work", work);
        model.addAttribute("pay", pay);
        model.addAttribute("backLink", "/pay/payment");
        if(!curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/pay/payment/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/human_resource/pay";
    }
    @GetMapping(value="/pay/payment/consider/workId={workId}")
    public String considerPayPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Pay pay = payRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("pay", pay);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/pay/payment/workId=" + workId);
        model.addAttribute("link", "/pay/payment/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/human_resource/pay";
    }
    
    // 수정 예정
    @PostMapping(value="/pay/payment/consider/workId={workId}")
    public String considerPay(@PathVariable("workId") Long workId, PayDto payDto, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        Pay pay = payRepository.findByWork(work);
        LocalDate payDate = payDto.getPayDate();
        payDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        pay.setPayDate(payDate);
        payRepository.save(pay);

        long total = pay.getTotalAdditional() + pay.getPayInfo().getBase();
        Payroll payroll = new Payroll();
        payroll.setNationalPension((long) Math.ceil(0.045 * total));
        payroll.setNationalHealthInsurance((long) Math.ceil(0.03545 * total));
        payroll.setEmploymentInsurance((long) Math.ceil(0.009 * total));
        payroll.setCorporateEmploymentInsurance((long) Math.ceil(0.0025 * total));
        payroll.setLongTermCareInsurance((long) Math.ceil(0.06405 * total));
        payroll.setIndustrialAccidentCompensationInsurance((long) Math.ceil(0.008 * total));
        payroll.setIncomeTax(payDto.getIncomeTax());
        payroll.setLocalIncomeTax((long) Math.ceil(payDto.getIncomeTax() * 0.1));
        payrollRepository.save(payroll);

        attributes.addFlashAttribute("alertData", "급여 지급이 완료되었습니다.");
        return "redirect:/pay/payment/workId=" + workId;
    }

    /* -> pay 통합
    @GetMapping(value="/human/work/payroll")
    public String payrollIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("급여 지급", "/payroll/pay");
        works.put("급여 명세서", "/payroll/payroll");

        model.addAttribute("headerTitle", "인사부 업무-급여 처리");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }
     */
}