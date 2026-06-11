package com.leavecalendar.service;

import com.leavecalendar.exception.ResourceNotFoundException;
import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getAllMembers() {
        return teamMemberRepository.findAll();
    }

    public TeamMember getById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found: " + id));
    }
}
