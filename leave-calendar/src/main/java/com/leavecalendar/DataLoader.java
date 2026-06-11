package com.leavecalendar;

import com.leavecalendar.model.TeamMember;
import com.leavecalendar.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void run(String... args) {
        if (teamMemberRepository.count() == 0) {
            List<TeamMember> members = List.of(
                    new TeamMember(null, "Alice"),
                    new TeamMember(null, "Bob"),
                    new TeamMember(null, "Charlie"),
                    new TeamMember(null, "Diana")
            );
            teamMemberRepository.saveAll(members);
            System.out.println("Sample team members loaded.");
        }
    }
}
