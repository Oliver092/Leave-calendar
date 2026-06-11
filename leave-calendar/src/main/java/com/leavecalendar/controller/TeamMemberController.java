package com.leavecalendar.controller;

import com.leavecalendar.model.TeamMember;
import com.leavecalendar.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping
    public ResponseEntity<List<TeamMember>> getAll() {
        return ResponseEntity.ok(teamMemberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMember> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teamMemberService.getById(id));
    }
}
