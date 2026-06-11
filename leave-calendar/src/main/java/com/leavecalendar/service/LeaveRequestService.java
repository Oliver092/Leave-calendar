package com.leavecalendar.service;

import com.leavecalendar.dto.LeaveRequestDTO;
import com.leavecalendar.exception.ResourceNotFoundException;
import com.leavecalendar.model.LeaveRequest;
import com.leavecalendar.model.LeaveStatus;
import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final TeamMemberService teamMemberService;

    public List<LeaveRequest> getAll() {
        return leaveRequestRepository.findAll();
    }

    public List<LeaveRequest> getByMember(Long memberId) {
        TeamMember member = teamMemberService.getById(memberId);
        return leaveRequestRepository.findByTeamMember(member);
    }

    public List<LeaveRequest> getByStatus(LeaveStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public LeaveRequest create(LeaveRequestDTO dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        TeamMember member = teamMemberService.getById(dto.teamMemberId());

        boolean overlap = leaveRequestRepository.existsOverlap(
                member, dto.startDate(), dto.endDate(), null);
        if (overlap) {
            throw new IllegalStateException("Leave request overlaps with an existing request");
        }

        LeaveRequest request = new LeaveRequest();
        request.setTeamMember(member);
        request.setStartDate(dto.startDate());
        request.setEndDate(dto.endDate());
        request.setReason(dto.reason());
        request.setStatus(LeaveStatus.PENDING);

        return leaveRequestRepository.save(request);
    }

    public LeaveRequest updateStatus(Long id, LeaveStatus newStatus) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));

        if (newStatus == LeaveStatus.APPROVED) {
            boolean overlap = leaveRequestRepository.existsOverlap(
                    request.getTeamMember(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getId()
            );
            if (overlap) {
                throw new IllegalStateException("Cannot approve: overlaps with another approved request");
            }
        }

        request.setStatus(newStatus);
        return leaveRequestRepository.save(request);
    }

    public void delete(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found: " + id);
        }
        leaveRequestRepository.deleteById(id);
    }
}
