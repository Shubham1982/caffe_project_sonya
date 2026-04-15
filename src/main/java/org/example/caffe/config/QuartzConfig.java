package org.example.caffe.config;

import lombok.extern.slf4j.Slf4j;
import org.example.caffe.domain.QuartzJobConfig;
import org.example.caffe.job.ProductSalesReconciliationJob;
import org.example.caffe.repository.QuartzJobConfigRepository;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class QuartzConfig {

    private static final String JOB_NAME = "productSalesReconciliationJob";
    private static final String DEFAULT_CRON = "0 0 * * * ?"; // every hour

    @Bean
    public CommandLineRunner initQuartzJobConfig(QuartzJobConfigRepository repository) {
        return args -> {
            if (repository.findByJobName(JOB_NAME).isEmpty()) {
                QuartzJobConfig config = QuartzJobConfig.builder()
                        .jobName(JOB_NAME)
                        .cronExpression(DEFAULT_CRON)
                        .isActive(true)
                        .description("Reconciles product_sales_qty_stats with actual order_items every hour")
                        .build();
                repository.save(config);
                log.info("Initialized Quartz job config in database: {}", JOB_NAME);
            }
        };
    }

    @Bean
    public JobDetail productSalesReconciliationJobDetail() {
        return JobBuilder.newJob(ProductSalesReconciliationJob.class)
                .withIdentity(JOB_NAME)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger productSalesReconciliationTrigger(JobDetail productSalesReconciliationJobDetail,
                                                     QuartzJobConfigRepository repository) {
        String cron = repository.findByJobName(JOB_NAME)
                .map(QuartzJobConfig::getCronExpression)
                .orElse(DEFAULT_CRON);

        log.info("Scheduling {} with cron: {}", JOB_NAME, cron);

        return TriggerBuilder.newTrigger()
                .forJob(productSalesReconciliationJobDetail)
                .withIdentity("productSalesReconciliationTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }
}
