package pt.scml.fin.batch.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobCompletionService {

    private final ControlService controlService;

    public JobCompletionService(ControlService controlService) {
        this.controlService = controlService;
    }

    public void completeJob(JobExecution jobExecution) {
        //    handleSuccessfulJob(jobExecution);

          //  handleFailedJob(jobExecution);

    }

    private void handleSuccessfulJob(JobExecution jobExecution) {

    }

    private void handleFailedJob(JobExecution jobExecution) {
        // Analyze failure type and handle appropriately

        controlService.markJobAsFailed();
    }
}