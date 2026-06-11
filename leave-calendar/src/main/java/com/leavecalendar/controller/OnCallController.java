package com.leavecalendar.controller;

import com.leavecalendar.service.OnCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oncall")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OnCallController {

    private final OnCallService onCallService;

    @GetMapping
    public ResponseEntity<List<OnCallService.OnCallWeek>> getSchedule(
            @RequestParam(defaultValue = "8") int weeks) {
        return ResponseEntity.ok(onCallService.getOnCallSchedule(weeks));
    }
}
