package com.ultimate.wellme.controllers;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ultimate.wellme.config.ApiResponse;

@RestController
@RequestMapping("/api/v1/scheduler")
public class SchedulerController {

    @Autowired
    private Scheduler scheduler;
    
    @PostMapping("/trigger-slot-generation")
    public ResponseEntity<ApiResponse> triggerSlotGeneration() {
        try {
            scheduler.triggerJob(JobKey.jobKey("slotGenerationJob"));
            return ResponseEntity.ok(new ApiResponse(true, "Slot generation job triggered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed to trigger job: " + e.getMessage()));
        }
    }
}
