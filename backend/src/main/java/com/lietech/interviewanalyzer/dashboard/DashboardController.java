package com.lietech.interviewanalyzer.dashboard;

import com.lietech.interviewanalyzer.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(
            DashboardService service
    ) {
        this.service = service;
    }

    @GetMapping
    public DashboardResponse getDashboard(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return service.getDashboard(principal);
    }

    @GetMapping("/progress")
    public List<ProgressResponse> getProgress(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return service.getProgress(principal);
    }
}