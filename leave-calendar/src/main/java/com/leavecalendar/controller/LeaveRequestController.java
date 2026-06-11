package com.leavecalendar.controller;

import com.leavecalendar.model.LeaveRequest;
import com.leavecalendar.model.LeaveStatus;
import com.leavecalendar.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.leavecalendar.dto.LeaveRequestDTO;


import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LeaveStatus status) {

        if (memberId != null) {
            return ResponseEntity.ok(leaveRequestService.getByMember(memberId));
        }
        if (status != null) {
            return ResponseEntity.ok(leaveRequestService.getByStatus(status));
        }
        return ResponseEntity.ok(leaveRequestService.getAll());
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> create(@RequestBody LeaveRequestDTO dto) {
        return ResponseEntity.ok(leaveRequestService.create(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LeaveRequest> updateStatus(
            @PathVariable Long id,
            @RequestParam LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}