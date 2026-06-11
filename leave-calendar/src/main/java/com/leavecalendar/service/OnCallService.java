package com.leavecalendar.service;

import com.leavecalendar.model.LeaveRequest;
import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OnCallService {

    private final TeamMemberService teamMemberService;
    private final LeaveRequestRepository leaveRequestRepository;

    public TeamMember getOnCallForWeek(LocalDate anyDayInWeek) {
        List<TeamMember> members = teamMemberService.getAllMembers();
        if (members.isEmpty()) return null;

        int weekNumber = anyDayInWeek.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        int index = (weekNumber - 1) % members.size();
        return members.get(index);
    }

    public List<OnCallWeek> getOnCallSchedule(int numberOfWeeks) {
        List<OnCallWeek> schedule = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        for (int i = 0; i < numberOfWeeks; i++) {
            LocalDate weekStart = monday.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);

            TeamMember onCall = getOnCallForWeek(weekStart);
            if (onCall == null) continue;

            List<LeaveRequest> conflicts = leaveRequestRepository
                    .findApprovedLeavesInPeriod(onCall, weekStart, weekEnd);

            schedule.add(new OnCallWeek(weekStart, weekEnd, onCall, !conflicts.isEmpty(), conflicts));
        }
        return schedule;
    }

    public record OnCallWeek(
            LocalDate weekStart,
            LocalDate weekEnd,
            TeamMember onCallMember,
            boolean hasConflict,
            List<LeaveRequest> conflictingLeaves
    ) {}
}
