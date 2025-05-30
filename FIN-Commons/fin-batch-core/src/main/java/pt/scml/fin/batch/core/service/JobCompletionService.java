package pt.scml.fin.batch.core.service;

import static pt.scml.fin.batch.core.utils.FinUtils.moveFileTo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.model.dto.ControlProcessDTO;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessFunctionalStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

@Slf4j
@Service
public class JobCompletionService {

    private final ContextCache contextCache;
    private final ContextHeader contextHeader;
    private final ControlService controlService;
    private final FinUtilsService finUtilsService;

    public JobCompletionService(ContextCache contextCache, ContextHeader contextHeader,
            ControlService controlService, FinUtilsService finUtilsService) {
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
        this.controlService = controlService;
        this.finUtilsService = finUtilsService;
    }

    public void completeJob(JobExecution jobExecution) {

        BatchStatus status = jobExecution.getStatus();
        if (status == BatchStatus.STOPPED || status == BatchStatus.FAILED) {
            handleJobFailed(jobExecution);
        } else {
            handleJobSuccess(jobExecution);
            log.info("{} job execution completed successfully", contextHeader.getFilename());
        }
    }

    /**
     * Handles a failed job execution by updating control records appropriately. If the job failed
     * due to a pre-existing process, creates a control process with error status. Otherwise, rolls
     * back any changes and updates the current process with error status.
     *
     * @param jobExecution The Spring Batch job execution context
     */
    private void handleJobFailed(JobExecution jobExecution) {
        String errorCode = "2";
        String errorMessage = "Module is already in execution";

        //TODO: review where to put this code
        List<Throwable> failureExceptions = jobExecution.getFailureExceptions();
        if (!failureExceptions.isEmpty()) {

            for (Throwable failureException : failureExceptions) {
                if (failureException instanceof JobWithModuleIdInExecutionException) {
                    if (contextHeader.isJobHasAFile()) {
                        contextHeader.setDuplicated(true);
                        moveFileTo(contextCache.getInputDirectory(),
                                contextCache.getDuplicatedDirectory(), contextHeader.getFilename());
                        errorCode = "1";
                        errorMessage =
                                "File: " + contextHeader.getFilename() + " is already processed";
                    }
                }
            }
        }

        if (contextHeader.getControlProcessId() != null) {
            this.finUtilsService.rollbackFinDailyInvoice(jobExecution.getJobInstance().getId());

            log.error("Job failed, updating control process with ERROR status");
            this.controlService.updateControlProcess(contextHeader.getControlProcessId(),
                    CtrlProcessStatusEnum.ERROR,
                    CtrlProcessFunctionalStatusEnum.ERROR,
                    "1", jobExecution.getFailureExceptions().getFirst().getMessage());

            List<Long> ctrlFileIds = this.controlService.findByProcessId(
                    contextHeader.getControlProcessId());

            for (Long ctrlFileId : ctrlFileIds) {
                this.controlService.updateControlFile(ctrlFileId,
                        CtrlFileStatusEnum.ERROR, 0L);
            }

        } else {

            if (contextHeader.isHasFileError()) {
                errorMessage = contextHeader.getFileErrorMessage();
            }

            // no control process created, probably job already in execution
            ControlProcessDTO ctrlProcess = this.controlService.createControlProcessError(
                    contextHeader.getModuleId(), contextHeader.getCurrFinPeriodId(), "",
                    contextHeader.getModuleShdes(), jobExecution.getJobInstance().getId(),
                    errorCode, errorMessage
            );

            if (contextHeader.isDuplicated()) {
                this.controlService.createControlFile(ctrlProcess.controlProcessId(),
                        LocalDateTime.now(),
                        DateUtils.getLocalDateTimeFromString(contextHeader.getProcDate(),
                                DateUtils.YYYYMMDD),
                        contextHeader.getFilename(),
                        0L,
                        CtrlFileStatusEnum.DUPLICATE.getCode(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        contextHeader.getUserId()
                );

            }
        }
    }

    /**
     * Handles a successful job execution by creating a control file record and updating the control
     * process status to SUCCESS.
     *
     * @param jobExecution The Spring Batch job execution context
     */
    private void handleJobSuccess(JobExecution jobExecution) {
        log.info("Job completed successfully, creating control file record");
        long writeCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        List<Long> ctrlFileIds = this.controlService.findByProcessId(
                contextHeader.getControlProcessId());
        for (Long ctrlFileId : ctrlFileIds) {
            controlService.updateControlFile(ctrlFileId,
                    CtrlFileStatusEnum.PROCESSED, writeCount);
        }

        log.info("Updating control process with SUCCESS status");
        this.controlService.updateControlProcess(contextHeader.getControlProcessId(),
                CtrlProcessStatusEnum.SUCCESS,
                CtrlProcessFunctionalStatusEnum.SUCCESS,
                "",
                "");
    }

}