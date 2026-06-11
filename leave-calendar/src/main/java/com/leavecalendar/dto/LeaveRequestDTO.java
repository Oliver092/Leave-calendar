package com.leavecalendar.dto;

import java.time.LocalDate;

public record LeaveRequestDTO(
        Long teamMemberId,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) {}
