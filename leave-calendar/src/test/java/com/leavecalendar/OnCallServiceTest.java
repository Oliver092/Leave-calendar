package com.leavecalendar;

import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.LeaveRequestRepository;
import com.leavecalendar.service.OnCallService;
import com.leavecalendar.service.TeamMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnCallServiceTest {

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private OnCallService onCallService;

    private List<TeamMember> members;

    @BeforeEach
    void setUp() {
        members = List.of(
                new TeamMember(1L, "Alice"),
                new TeamMember(2L, "Bob"),
                new TeamMember(3L, "Charlie"),
                new TeamMember(4L, "Diana")
        );
    }

    @Test
    void getOnCallSchedule_returnsCorrectNumberOfWeeks() {
        when(teamMemberService.getAllMembers()).thenReturn(members);
        when(leaveRequestRepository.findApprovedLeavesInPeriod(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<OnCallService.OnCallWeek> schedule = onCallService.getOnCallSchedule(4);

        assertEquals(4, schedule.size());
    }

    @Test
    void getOnCallSchedule_noConflict_whenNoApprovedLeave() {
        when(teamMemberService.getAllMembers()).thenReturn(members);
        when(leaveRequestRepository.findApprovedLeavesInPeriod(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<OnCallService.OnCallWeek> schedule = onCallService.getOnCallSchedule(4);

        schedule.forEach(week -> assertFalse(week.hasConflict()));
    }

    @Test
    void getOnCallSchedule_hasConflict_whenApprovedLeaveExists() {
        when(teamMemberService.getAllMembers()).thenReturn(members);

        // Első héten conflict, többinél nem
        when(leaveRequestRepository.findApprovedLeavesInPeriod(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(leaveRequestRepository.findApprovedLeavesInPeriod(
                eq(members.get(0)), any(), any()))
                .thenReturn(List.of(new com.leavecalendar.model.LeaveRequest()));

        List<OnCallService.OnCallWeek> schedule = onCallService.getOnCallSchedule(4);

        // Legalább egy hétnek conflict-ja van
        assertTrue(schedule.stream().anyMatch(OnCallService.OnCallWeek::hasConflict));
    }

    @Test
    void getOnCallForWeek_emptyTeam_returnsNull() {
        when(teamMemberService.getAllMembers()).thenReturn(Collections.emptyList());

        TeamMember result = onCallService.getOnCallForWeek(LocalDate.now());

        assertNull(result);
    }

    @Test
    void getOnCallSchedule_rotationRepeats_after4Weeks() {
        when(teamMemberService.getAllMembers()).thenReturn(members);
        when(leaveRequestRepository.findApprovedLeavesInPeriod(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<OnCallService.OnCallWeek> schedule = onCallService.getOnCallSchedule(8);

        // Az 1. és 5. hét ugyanaz a személy
        assertEquals(
                schedule.get(0).onCallMember().getName(),
                schedule.get(4).onCallMember().getName()
        );
    }
}
