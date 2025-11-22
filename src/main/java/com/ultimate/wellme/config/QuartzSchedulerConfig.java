package com.ultimate.wellme.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ultimate.wellme.jobs.SlotGenerationJob;

@Configuration
public class QuartzSchedulerConfig {
    
    @Bean
    public JobDetail slotGenerationJobDetail() {
        return JobBuilder.newJob(SlotGenerationJob.class)
                    .withIdentity("slotGenerationJob")
                    .withDescription("Job to generate appointment slots")
                    .storeDurably()
                    .build();
    }

    @Bean
    public Trigger slotGenerationTrigger() {
        return TriggerBuilder.newTrigger()
                    .forJob(slotGenerationJobDetail())
                    .withIdentity("slotGenerationTrigger")
                    .withDescription("Trigger for slot generation job")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                    .build();
    }

    // Manual trigger for testing
    @Bean
    public Trigger slotGenerationManualTrigger() {
        // Run every 5 minutes for testing (disable in production)
        return TriggerBuilder.newTrigger()
                    .forJob(slotGenerationJobDetail())
                    .withIdentity("slotGenerationManualTrigger")
                    .withDescription("Manual trigger for testing")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(5).repeatForever())
                    .build();
    }
}
