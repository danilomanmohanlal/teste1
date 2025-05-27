package pt.scml.fin.batch.core.listener;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.service.JobCompletionService;
import pt.scml.fin.batch.core.service.JobPreparationService;
import pt.scml.fin.batch.core.utils.DateUtils;

@Slf4j
@Component
public class JobControlListener implements JobExecutionListener {

    private final JobPreparationService jobPreparationService;
    private final JobCompletionService jobCompletionService;

    public JobControlListener(JobPreparationService jobPreparationService,
            JobCompletionService jobCompletionService) {
        this.jobPreparationService = jobPreparationService;
        this.jobCompletionService = jobCompletionService;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting job preparation for execution: {}", jobExecution.getJobInstance().getJobName());
        jobPreparationService.prepareJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Completing job execution: {} with status: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus());
        jobCompletionService.completeJob(jobExecution);
    }


}
