package com.leavecalendar;

import com.leavecalendar.dto.LeaveRequestDTO;
import com.leavecalendar.exception.ResourceNotFoundException;
import com.leavecalendar.model.LeaveRequest;
import com.leavecalendar.model.LeaveStatus;
import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.LeaveRequestRepository;
import com.leavecalendar.service.LeaveRequestService;
import com.leavecalendar.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private TeamMember alice;
    private LeaveRequestDTO validDTO;

    @BeforeEach
    void setUp() {
        alice = new TeamMember(1L, "Alice");
        validDTO = new LeaveRequestDTO(
                1L,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                "Vacation"
        );
    }

    @Test
    void create_validRequest_savesSuccessfully() {
        when(teamMemberService.getById(1L)).thenReturn(alice);
        when(leaveRequestRepository.existsOverlap(any(), any(), any(), any())).thenReturn(false);
        when(leaveRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest result = leaveRequestService.create(validDTO);

        assertNotNull(result);
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        assertEquals(alice, result.getTeamMember());
        verify(leaveRequestRepository, times(1)).save(any());
    }

    @Test
    void create_endDateBeforeStartDate_throwsIllegalArgument() {
        LeaveRequestDTO invalidDTO = new LeaveRequestDTO(
                1L,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 5),
                "Invalid"
        );

//        when(teamMemberService.getById(1L)).thenReturn(alice);

        assertThrows(IllegalArgumentException.class,
                () -> leaveRequestService.create(invalidDTO));

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void create_overlappingRequest_throwsIllegalState() {
        when(teamMemberService.getById(1L)).thenReturn(alice);
        when(leaveRequestRepository.existsOverlap(any(), any(), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> leaveRequestService.create(validDTO));

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void updateStatus_notFound_throwsResourceNotFound() {
        when(leaveRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestService.updateStatus(99L, LeaveStatus.APPROVED));
    }

    @Test
    void updateStatus_approveWithOverlap_throwsIllegalState() {
        LeaveRequest existing = new LeaveRequest();
        existing.setId(1L);
        existing.setTeamMember(alice);
        existing.setStartDate(LocalDate.of(2026, 7, 1));
        existing.setEndDate(LocalDate.of(2026, 7, 5));
        existing.setStatus(LeaveStatus.PENDING);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(leaveRequestRepository.existsOverlap(any(), any(), any(), eq(1L))).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> leaveRequestService.updateStatus(1L, LeaveStatus.APPROVED));
    }
}
