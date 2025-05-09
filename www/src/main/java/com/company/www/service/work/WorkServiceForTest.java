package com.company.www.service.work;

import com.company.www.constant.work.ApprovalPosition;
import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.ApprovalLine;
import com.company.www.entity.work.Approver;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.exception.UnauthorizedException;
import com.company.www.repository.belong.PositionRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.ApprovalLineRepository;
import com.company.www.repository.work.ApproverRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WorkServiceForTest {
    @Autowired
    StaffRepository staffRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;
    @Autowired
    ApprovalLineRepository approvalLineRepository;
    @Autowired
    ApproverRepository approverRepository;

    private final Map<Integer, ApprovalPosition> APPROVAL_POSITION_MAP = Map.ofEntries(
            Map.entry(0, ApprovalPosition.NOT_POSITION),
            Map.entry(120, ApprovalPosition.PRESIDENT),
            Map.entry(110, ApprovalPosition.VICE_PRESIDENT),
            Map.entry(90, ApprovalPosition.MANAGING_DIRECTOR),
            Map.entry(70, ApprovalPosition.GENERAL_MANAGER),
            Map.entry(60, ApprovalPosition.DEPUTY_GENERAL_MANAGER),
            Map.entry(50, ApprovalPosition.MANAGER),
            Map.entry(40, ApprovalPosition.ASSISTANT_MANAGER),
            Map.entry(30, ApprovalPosition.ASSOCIATE),
            Map.entry(20, ApprovalPosition.STAFF),
            Map.entry(10, ApprovalPosition.INTERN)
    );

    // 중간 권한 체크
    // 매개변수에 들어올 값의 정보
    // 0 : 미정, 100 : 사장, 90 : 부사장, 80 : 상무이사
    // 70 : 부장, 60 : 차장, 50 : 과장, 40 : 대리, 30 : 주임, 20 : 사원,  10 : 인턴
    // 직급 우선순위 비교
    // prior (이전 결재자의 직급)과 staff (현재 결재자의 직급)을 비교하여, staff가 우선순위가 높은지 확인
    private boolean higherThanPrior(int prior, int staff) {
        // 이전 결재자의 직급(prior)보다 staff의 직급이 높은 경우 true를 반환
        return prior < staff;
    }

    // Proxy 상태 체크
    // ApprovalState가 "대결" 또는 "전대결"일 경우 Proxy 상태로 간주
    private boolean isProxy(ApprovalState approvalState) {
        // ApprovalState 코드가 "대결" 또는 "전대결"이면 Proxy로 간주
        return "대결".equals(approvalState.getCode()) || "전대결".equals(approvalState.getCode());
    }

    // 최종 권한 체크
    // prior : 이전 결재자의 직급
    // approval : 결재선의 직급 코드 (결재 대기 중인 직급)
    // staff : 현재 결재자의 직급
    // state : ApprovalState (결재 상태)
    private boolean hasApprovalAuthority(int prior, int approval, int staff, ApprovalState state) {
        // Proxy 상태일 경우
        if (isProxy(state)) {
            // Proxy 상태일 때는 특정 직급 이상일 경우 (staff >= 80 && approval > 70)
            // 또는 prior < staff 이고 approval이 staff 이상인 경우 권한이 있다고 판단
            return (staff >= 80 && approval > 70) || (higherThanPrior(prior, staff) && approval >= staff);
        }
        // Proxy가 아닐 경우, 단순히 prior보다 staff가 우선순위가 높고, approval이 staff와 동일해야 권한이 있다고 판단
        return higherThanPrior(prior, staff) && approval == staff;
    }

    // 이사 이상 Proxy 권한 체크
    // 이사 이상 직급의 경우, Proxy 상태와 결재자가 있는지를 확인하여 권한을 부여
    public boolean hasDirectorProxyAuthority(Work work, ApprovalState approvalState) {
        // 결재자가 Draft로 지정한 직원의 직급을 가져옴
        Position draftPosition = work.getDraftStaff().getPosition();
        // 결재자 리스트를 가져옴
        List<Approver> approvers = work.getApprovers();

        // 이사 이상 직급이고, Proxy 상태일 때 결재자가 있다면 권한을 부여
        return draftPosition.getPositionRank() >= 80 && isProxy(approvalState) && !approvers.isEmpty();
    }

    // 결재 권한 확인
    // 결재자가 결재선에서 통과할 차례인지, 그리고 결재 권한을 가지고 있는지 확인
    public boolean checkApprovalAuthority(Work work, Staff approvalStaff, ApprovalState approvalState) {
        // 결재자 리스트를 가져옴
        List<Approver> approvers = work.getApprovers();
        // 결재선에서 각 결재자의 직급을 가져옴
        List<ApprovalPosition> approvalPositions = work.getApprovalLine().getApprovalPositions();

        // 결재자가 모든 결재선을 이미 통과한 경우, 추가적인 권한 체크는 불필요
        if (approvers.size() == approvalPositions.size()) {
            return false;
        }

        // priorAuthority : 이전 결재자의 직급 (approvers 리스트에서 마지막 결재자의 직급)
        // approvalAuthority : 현재 결재선의 직급 코드
        // staffAuthority : 현재 결재자의 직급
        int priorAuthority = approvers.isEmpty() ?
                work.getDraftStaff().getPosition().getPositionRank() :
                approvers.get(approvers.size() - 1).getApprovalStaff().getPosition().getPositionRank();

        int approvalAuthority = approvalPositions.get(approvers.size()).getCode();
        int staffAuthority = approvalStaff.getPosition().getPositionRank();

        // 결재 권한 확인 : 권한이 있거나 상무이사 이상의 Proxy 권한이 있는 경우 true
        return hasApprovalAuthority(priorAuthority, approvalAuthority, staffAuthority, approvalState) ||
                hasDirectorProxyAuthority(work, approvalState);
    }

    // 결재 라인 설정(기본값)
    // 주어진 직원의 직급에 따라 기본 결재 라인을 설정하는 메서드
    public ApprovalLine makeDefaultApprovalLine(WorkType workType, Staff draftStaff) {
        // 직원의 직급명 가져오기
        String positionName = draftStaff.getPosition().getPositionName().trim();
        Map<String, List<ApprovalPosition>> defaultApprovalMap = Map.of(
                "사장", List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT),
                "부사장", List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT),
                "상무이사", List.of(ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT),
                "부장", List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT),
                "차장", List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT),
                "과장", List.of(ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT),
                "대리", List.of(ApprovalPosition.MANAGER, ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT),
                "주임", List.of(ApprovalPosition.MANAGER, ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT),
                "사원", List.of(ApprovalPosition.MANAGER, ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT)
        );

        List<ApprovalPosition> positions = defaultApprovalMap.getOrDefault(positionName, List.of());

        // 직급에 따라 결재 라인 설정

        // 결재 역할 설정: 작업 타입이 "공통"이면 직원의 역할을 사용, 아니면 작업 타입의 역할 사용
        Role role = workType.getRole().getRoleName().equals("공통") ? draftStaff.getRole() : workType.getRole();

        // 결재 라인 객체 생성
        ApprovalLine approvalLine = new ApprovalLine();
        approvalLine.setApprovalPositions(positions); // 설정된 결재 직급들
        approvalLine.setRole(role); // 역할 설정
        approvalLine.setApproverSize(positions.size()); // 결재자가 몇 명인지 설정

        // 결재 라인을 데이터베이스에 저장하고 반환
        return approvalLineRepository.save(approvalLine);
    }

    // 결재라인 설정(직접 설정)
    // 주어진 결재 직급 리스트에 맞춰 결재 라인을 설정하는 메서드
    public ApprovalLine makeApprovalLine(String ... approvalPositions) {
        // 결재 라인 객체 생성
        ApprovalLine approvalLine = new ApprovalLine();
        List<ApprovalPosition> positions = new ArrayList<>();

        // 결재 직급 리스트를 받아서 해당 직급의 ApprovalPosition을 찾아서 추가
        for(String approvalPosition : approvalPositions){
            for (ApprovalPosition pos : ApprovalPosition.values()) {
                if (pos.getPosition().equals(approvalPosition)) {
                    positions.add(pos);
                    break;
                }
            }
        }

        // 결재자 수 설정
        approvalLine.setApproverSize(positions.size());

        // 결재 라인을 데이터베이스에 저장하고 반환
        return approvalLineRepository.save(approvalLine);
    }

    public WorkType setArbitraryApprovalLine(WorkType workType, String arbitraryPosition){
        Position position = positionRepository.findByPositionName(arbitraryPosition);
        workType.setArbitraryPosition(position);
        return workTypeRepository.save(workType);
    }

    // 문서 생성
    public Work initiateWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel, ApprovalLine approvalLine){
        Work work = new Work();
        work.setWorkType(workType);
        work.setSubject(subject);
        work.setRetentionPeriod(retentionPeriod);
        work.setSecurityLevel(securityLevel);

        work.setDraftStaff(draftStaff);
        work.setDraftDate(LocalDateTime.now());
        work.setDraftState(WorkState.PENDING);
        work.setApprovers(new ArrayList<>());
        work.setApprovalLine(approvalLine);
        return work;
    }

    public Work createWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel){
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, draftStaff, subject, retentionPeriod, securityLevel, makeDefaultApprovalLine(workType, draftStaff));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("문서를 생성할 권한이 없습니다.");
        }
    }

    // 결재라인 설정에 대해서는 차후 구현 예정
    public Work createWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel, String ... positions){
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, draftStaff, subject, retentionPeriod, securityLevel, makeApprovalLine(positions));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }

    public Work updateWork(Work work, String subject, String retentionPeriod, String securityLevel){
        if(!work.getDraftState().equals(WorkState.PENDING)){
            if(work.getDraftState().equals(WorkState.HANDLING)){
                throw new UnauthorizedException("결재 중인 문서입니다.");
            }
            else if(work.getDraftState().equals(WorkState.COMPLETED)){
                throw new UnauthorizedException("결재 완료된 문서입니다.");
            }
            else {
                throw new UnauthorizedException("기타 사유로 수정할 수 없습니다.(사유 : " + work.getDraftState().getCode() + ")");
            }
        }
        work.setSubject(subject);
        work.setRetentionPeriod(retentionPeriod);
        work.setSecurityLevel(securityLevel);
        return workRepository.save(work);
    }

    // 결재
    public Work approvalWork(Work work, Staff approvalStaff, ApprovalState approvalState){
        boolean checkAuthority = checkApprovalAuthority(work, approvalStaff, approvalState);

        if(checkAuthority){
            List<Approver> approvers = work.getApprovers();
            int approverSize = work.getApprovalLine().getApproverSize();

            Approver approver = new Approver();
            approver.setApprovalStaff(approvalStaff);
            approver.setApprovalTime(LocalDateTime.now());
            approver.setApprovalState(approvalState);
            approvers.add(approverRepository.save(approver));

            boolean isTopLevelApproval = approvalStaff.getPosition().getPositionRank() >= 80 && approvalState.equals(ApprovalState.PROXY);

            boolean isHandling = approvalState.equals(ApprovalState.APPROVE) || approvalState.equals(ApprovalState.DEFER) || approvalState.equals(ApprovalState.PROXY);

            boolean finalCheck = isTopLevelApproval || !isHandling || (approverSize == approvers.size());

            work.setDraftState(finalCheck ? WorkState.COMPLETED : WorkState.HANDLING);
            work.setApprovers(approvers);
            return workRepository.save(work);
        }
        else{
            if(work.getDraftState().equals(WorkState.COMPLETED) || work.getDraftState().equals(WorkState.DELETE)
                    || work.getDraftState().equals(WorkState.DISPLAY) || work.getDraftState().equals(WorkState.OVERDUE)){
                throw new IllegalStateException("결재할 수 없는 문서입니다. 사유 : " + work.getDraftState().getCode());
            }
            else {
                throw new UnauthorizedException("결재 권한이 없습니다.");
            }
        }
    }

    public Work approveWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.APPROVE);
    }

    public Work deferWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.DEFER);
    }

    public Work rejectWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.REJECT);
    }

    public Work arbitraryWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.ARBITRARY);
    }

    public Work proxyWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.PROXY);
    }

    public Work arbitraryProxyWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.ARBITRARY_PROXY);
    }

    public void displayDraftWork(Work work){
        System.out.println("-----");
        System.out.println("업무 이름 : " + work.getWorkType().getWorkName());
        System.out.println("업무 유형 : " + work.getWorkType().getRole().getRoleName());
        System.out.println("-----");
        System.out.println("제목 : " + work.getSubject());
        System.out.println("보존년한 : " + work.getRetentionPeriod());
        System.out.println("보안등급 : " + work.getSecurityLevel());
        System.out.println("-----");
        System.out.println("-----");
        System.out.println("기안자명 : " + work.getDraftStaff().getStaffName());
        System.out.println("부 : " + work.getDraftStaff().getDepartment().getDepartmentName());
        System.out.println("과 : " + work.getDraftStaff().getSection().getSectionName());
        System.out.println("직위 : " + work.getDraftStaff().getPosition().getPositionName());
        System.out.println("직무 : " + work.getDraftStaff().getRole().getRoleName());
        System.out.println("기안 날짜 : " + work.getDraftDate().format(DateTimeFormatter.ofPattern("y-M-d h:m:s")));
        System.out.println("결재 상태 : " + work.getDraftState().getCode());
        System.out.println("-----");
    }

    public void displayApprovalWork(Work work){
        displayDraftWork(work);
        List<Approver> approvers = work.getApprovers();
        for(int i = 0; i < work.getApprovers().size(); i++){
            System.out.println((i+1) + "차 결재자명 : " + approvers.get(i).getApprovalStaff().getStaffName());
            System.out.println("부 : " + approvers.get(i).getApprovalStaff().getDepartment().getDepartmentName());
            System.out.println("과 : " + approvers.get(i).getApprovalStaff().getSection().getSectionName());
            System.out.println("직위 : " + approvers.get(i).getApprovalStaff().getPosition().getPositionName());
            System.out.println("직무 : " + approvers.get(i).getApprovalStaff().getRole().getRoleName());
            System.out.println("결재 날짜 : " + approvers.get(i).getApprovalTime().format(DateTimeFormatter.ofPattern("y-M-d h:m:s")));
            System.out.println("결재 상태 : " + approvers.get(i).getApprovalState().getCode());
            System.out.println("-----");
        }
    }

    public void displayApprovalLine(ApprovalLine approvalLine){
        List<ApprovalPosition> positions = approvalLine.getApprovalPositions();
        if(positions.isEmpty()){
            System.out.println("설정된 결재라인이 없습니다.");
        }
        for(ApprovalPosition position : positions){
            System.out.println(position.getPosition());
        }
    }
}
