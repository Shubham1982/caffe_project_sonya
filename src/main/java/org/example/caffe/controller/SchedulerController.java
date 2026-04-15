package org.example.caffe.controller;

import org.example.caffe.domain.QuartzJobConfig;
import org.example.caffe.error.ResourceNotFoundException;
import org.example.caffe.repository.QuartzJobConfigRepository;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    private final QuartzJobConfigRepository configRepository;
    private final Scheduler scheduler;

    public SchedulerController(QuartzJobConfigRepository configRepository, Scheduler scheduler) {
        this.configRepository = configRepository;
        this.scheduler = scheduler;
    }

    @GetMapping("/config/{jobName}")
    public QuartzJobConfig getConfig(@PathVariable String jobName) {
        return configRepository.findByJobName(jobName)
                .orElseThrow(() -> new ResourceNotFoundException("Job config not found: " + jobName));
    }

    @PutMapping("/config/{jobName}")
    public String updateCron(@PathVariable String jobName, @RequestParam String cronExpression) {
        QuartzJobConfig config = configRepository.findByJobName(jobName)
                .orElseThrow(() -> new ResourceNotFoundException("Job config not found: " + jobName));

        config.setCronExpression(cronExpression);
        configRepository.save(config);

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey("productSalesReconciliationTrigger");
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);

            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .forJob(oldTrigger.getJobKey())
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (Exception e) {
            return "Cron saved to DB but failed to reschedule live trigger: " + e.getMessage();
        }

        return "Cron updated to '" + cronExpression + "' and job rescheduled successfully";
    }

    @PostMapping("/trigger/{jobName}")
    public String triggerJobManually(@PathVariable String jobName) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName));
            return "Job '" + jobName + "' triggered manually";
        } catch (Exception e) {
            return "Failed to trigger job: " + e.getMessage();
        }
    }
}
