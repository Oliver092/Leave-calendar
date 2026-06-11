package com.leavecalendar.repository;

import com.leavecalendar.model.LeaveRequest;
import com.leavecalendar.model.LeaveStatus;
import com.leavecalendar.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByTeamMember(TeamMember teamMember);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByTeamMemberAndStatus(TeamMember teamMember, LeaveStatus status);

    @Query("""
        SELECT COUNT(lr) > 0 FROM LeaveRequest lr
        WHERE lr.teamMember = :member
        AND lr.status != 'REJECTED'
        AND lr.startDate <= :endDate
        AND lr.endDate >= :startDate
        AND (:excludeId IS NULL OR lr.id != :excludeId)
    """)
    boolean existsOverlap(
            @Param("member") TeamMember member,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );

    @Query("""
        SELECT lr FROM LeaveRequest lr
        WHERE lr.teamMember = :member
        AND lr.status = 'APPROVED'
        AND lr.startDate <= :endDate
        AND lr.endDate >= :startDate
    """)
    List<LeaveRequest> findApprovedLeavesInPeriod(
            @Param("member") TeamMember member,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
