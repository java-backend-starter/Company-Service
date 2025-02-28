package com.company.www.service.work;


import com.company.www.constant.work.ApprovalPosition;
import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.dto.work.WorkDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WorkService {

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

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ApproverRepository approverRepository;

    @Autowired
    private ApprovalLineRepository approvalLineRepository;

    @Autowired
    private PositionRepository positionRepository;

    // 중간 권한 체크
    // 매개변수에 들어올 값의 정보
    // 0 : 미정, 100 : 사장, 90 : 부사장, 80 : 상무이사
    // 70 : 부장, 60 : 차장, 50 : 과장, 40 : 대리, 30 : 주임, 20 : 사원,  10 : 인턴

    private boolean higherThanPrior(int prior, int staff){
        return (prior < staff);
    }

    private boolean isProxy(ApprovalState approvalState){
        return approvalState.getCode().equals("대결") || approvalState.getCode().equals("전대결");
    }

    // 최종 권한 체크
    private boolean hasApprovalAuthority(int prior, int approval, int staff, ApprovalState state){
        if(isProxy(state)){
            return (staff >= 80 && approval > 70) || (higherThanPrior(prior, staff) && (approval >= staff));
        }
        return higherThanPrior(prior,  staff) && (approval == staff);
    }

    public boolean hasDirectorProxyAuthority(Work work, ApprovalState approvalState){
        // 직급에 부여된 우선순위 번호
        // 0 : 미정, 100 : 사장, 90 : 부사장, 80 : 상무이사
        // 70 : 부장, 60 : 차장, 50 : 과장, 40 : 대리, 30 : 주임, 20 : 사원,  10 : 인턴
        Position draftPosition = work.getDraftStaff().getPosition();
        List<Approver> approvers = work.getApprovers();

        return draftPosition.getPositionRank() >= 80 && isProxy(approvalState) && !approvers.isEmpty();
    }

    public boolean checkApprovalAuthority(Work work, Staff approvalStaff, ApprovalState approvalState){
        List<Approver> approvers = work.getApprovers();
        List<ApprovalPosition> approvalPositions = work.getApprovalLine().getApprovalPositions();

        if(approvers.size() == approvalPositions.size()){
            return false;
        }

        int prior = (approvers.isEmpty()) ?
                work.getDraftStaff().getPosition().getPositionRank() : approvers.get(approvers.size()-1).getApprovalStaff().getPosition().getPositionRank();

        int approval = approvalPositions.get(approvers.size()).getCode();
        int staff = approvalStaff.getPosition().getPositionRank();

        return hasApprovalAuthority(prior, approval, staff, approvalState) || hasDirectorProxyAuthority(work, approvalState);
    }

    // 결재 라인 설정
    public ApprovalLine makeDefaultApprovalLine(WorkType workType, Staff draftStaff){
        String positionName = draftStaff.getPosition().getPositionName();
        List<ApprovalPosition> positions;
        switch(positionName){
            case "사장", "부장" -> {
                positions = List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT);
            }
            case "부사장" -> {
                positions = List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT);
            }
            case "상무이사"-> {
                positions = List.of(ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT);
            }
            case "차장", "과장" -> {
                positions = List.of(ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT);
            }
            default -> {
                positions = List.of(ApprovalPosition.MANAGER, ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR);
            }
        }

        Role role = workType.getRole().getRoleName().equals("공통") ? draftStaff.getRole() : workType.getRole();

        ApprovalLine approvalLine = new ApprovalLine();
        approvalLine.setApprovalPositions(positions);
        approvalLine.setRole(role);
        approvalLine.setApproverSize(positions.size());
        return approvalLineRepository.save(approvalLine);
    }

    public ApprovalLine makeApprovalLine(String ... approvalPositions){
        ApprovalLine approvalLine = new ApprovalLine();
        List<ApprovalPosition> positions = new ArrayList<>();
        for(String approvalPosition : approvalPositions){
            Position position = positionRepository.findByPositionName(approvalPosition);
            if(position != null){
                ApprovalPosition ap = APPROVAL_POSITION_MAP.get(position.getPositionRank());
                if(ap != null){
                    positions.add(ap);
                }
            }
        }
        approvalLine.setApproverSize(positions.size());
        return approvalLineRepository.save(approvalLine);
    }

    public WorkType setArbitraryApprovalLine(WorkType workType, String arbitraryPosition){
        Position position = positionRepository.findByPositionName(arbitraryPosition);
        workType.setArbitraryPosition(position);
        return workTypeRepository.save(workType);
    }

    // 문서 생성
    public Work initiateWork(WorkType workType, WorkDto workDto, Staff draftStaff, ApprovalLine approvalLine){
        Work work = new Work();
        work.setWorkType(workType);
        work.setSubject(workDto.getSubject());
        work.setRetentionPeriod(workDto.getRetentionPeriod());
        work.setSecurityLevel(workDto.getSecurityLevel());

        work.setDraftStaff(draftStaff);
        work.setDraftDate(LocalDateTime.now());
        work.setDraftState(WorkState.PENDING);
        work.setApprovers(new ArrayList<>());
        work.setApprovalLine(approvalLine);
        return work;
    }

    public Work createWork(WorkType workType, WorkDto workDto){
        Staff draftStaff = staffRepository.findByUserId(workDto.getDraftStaff());
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, workDto, draftStaff, makeDefaultApprovalLine(workType, draftStaff));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("문서를 생성할 권한이 없습니다.");
        }
    }

    // 결재라인 설정에 대해서는 차후 구현 예정
    public Work createWork(WorkType workType, WorkDto workDto, String ... positions){
        Staff draftStaff = staffRepository.findByUserId(workDto.getDraftStaff());
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, workDto, draftStaff, makeApprovalLine(positions));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }

    public Work updateWork(Work work, WorkDto workDto){
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
        work.setSubject(workDto.getSubject());
        work.setRetentionPeriod(workDto.getRetentionPeriod());
        work.setSecurityLevel(workDto.getSecurityLevel());
        return workRepository.save(work);
    }

    // 결재
    public Work approvalWork(Work work, Staff approvalStaff, ApprovalState approvalState){
        boolean checkAuthority = checkApprovalAuthority(work, approvalStaff, approvalState);

        if(checkAuthority){
            List<Approver> approvers = work.getApprovers();

            Approver approver = new Approver();
            approver.setApprovalStaff(approvalStaff);
            approver.setApprovalTime(LocalDateTime.now());
            approver.setApprovalState(approvalState);
            approvers.add(approverRepository.save(approver));

            boolean isCompleted = work.getApprovalLine().getApprovalPositions().size() == approvers.size();

            boolean isTopLevelApproval = approvalStaff.getPosition().getPositionRank() >= 80 && approvalState.equals(ApprovalState.PROXY);

            boolean isHandling = approvalState.equals(ApprovalState.APPROVE) || approvalState.equals(ApprovalState.DEFER) || approvalState.equals(ApprovalState.PROXY);

            boolean finalCheck = isTopLevelApproval || !isHandling || isCompleted;

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
}
