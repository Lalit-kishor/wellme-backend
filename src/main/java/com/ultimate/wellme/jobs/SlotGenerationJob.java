package com.ultimate.wellme.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ultimate.wellme.services.SlotGenerationService;

@Component
public class SlotGenerationJob implements Job{

    private static final Logger logger = LoggerFactory.getLogger(SlotGenerationJob.class);

    @Autowired
    private SlotGenerationService slotGenerationService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            logger.info("Starting slot generation job...");

            // Generate slots for next 7 days
            slotGenerationService.generateSlotsForNextWeek();

            logger.info("Slot generation job completed successfully");

        } catch (Exception e) {
            logger.error("Error in slot generation job: ", e);
            throw new JobExecutionException(e);            
        }
    }
    
}
