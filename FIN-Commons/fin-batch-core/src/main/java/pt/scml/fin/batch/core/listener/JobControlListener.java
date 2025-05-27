package pt.scml.fin.batch.core.listener;

import static pt.scml.fin.batch.core.utils.FinUtils.moveFileTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.exceptions.JobAlreadyCompletedException;
import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;
import pt.scml.fin.batch.core.service.ControlService;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.model.dto.ControlFileDTO;
import pt.scml.fin.model.dto.ControlProcessDTO;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessFunctionalStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

/**
 * Listener for controlling job execution for Euromilhoes processing. Manages the lifecycle of
 * control processes including validation, preparation, and completion.
 * <p>
 * This listener ensures proper tracking and management of job execution by:
 * <ul>
 *   <li>Checking for existing processes before starting a new one</li>
 *   <li>Managing financial cycles and periods</li>
 *   <li>Creating and updating control records</li>
 *   <li>Handling successful and failed job executions</li>
 * </ul>
 */
@Slf4j
@Component
public class JobControlListener implements JobExecutionListener {

    private static final String CONFIG_KEY_CURRENT_PERIOD = "CURR_PERIOD_INV";

    private final ContextHeader contextHeader;

    private final ContextCache contextCache;

    private final ControlService controlService;

    private final FinUtilsService finUtilsService;

    private ControlProcessDTO currentProcess;

    private ControlFileDTO currentControlFile;

    @Autowired
    public JobControlListener(ContextHeader contextHeader, ContextCache contextCache,
            ControlService controlService,
            FinUtilsService finUtilsService) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.controlService = controlService;
        this.finUtilsService = finUtilsService;
    }

    /**
     * Prepares the job execution by:
     * <ol>
     *   <li>Checking for existing processes</li>
     *   <li>Generating a user ID for job tracking</li>
     *   <li>Preparing financial cycles</li>
     *   <li>Creating a control process record</li>
     *   <li>Verifying the module hasn't already been processed</li>
     *   <li>Setting required parameters for job execution</li>
     * </ol>
     *
     * @param jobExecution The Spring Batch job execution context
     * @throws RuntimeException If a process for the same date is already executing or has been
     *                          executed
     */
    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {

        log.info("Preparing {} job execution", contextHeader.getFilename());

        if (!contextHeader.isJobParamsValid()) {
            throw new IllegalArgumentException(contextHeader.getParamsExceptionMessage());
        }

        log.info("Validate if there is already an job in execution or executed for {}",
                DateUtils.getStringFromLocalDate(LocalDate.now(), DateUtils.YYYYMMDD));

        Long moduleId = this.finUtilsService.getFinModuleId(contextHeader.getModuleShdes());
        String currFinPeriodIdStr = this.finUtilsService.getConfigValue(CONFIG_KEY_CURRENT_PERIOD);
        Long currFinPeriodId = Long.parseLong(currFinPeriodIdStr);

        contextHeader.setCurrFinPeriodId(currFinPeriodId);
        contextHeader.setModuleId(moduleId);
        contextHeader.setJobExecutionId(jobExecution.getJobInstance().getId());

        //define generic user id job_name + job_instance_id + LocalDate.now
        generateUserId(jobExecution);

        checkForExistingProcessesForControlProcess(moduleId);

        checkForExecutedProcessesForControlFile();

        //prepare financial cycles
        prepareCycles();

        //create control process
        createControlProcess(moduleId, currFinPeriodId, jobExecution);

        //create control file
        createControlFile();

        contextHeader.setControlProcessId(currentProcess.controlProcessId());

        //check processed modules
        checkProcessedModules(jobExecution);
    }

    private void checkForExecutedProcessesForControlFile() {
        this.controlService.checkCtrlFileAlreadyExecuted(contextHeader.getFilename(),
                contextHeader.getModuleId());
    }

    /**
     * Completes the job execution by updating control records.
     * <p>
     * If the job failed, updates the control process with ERROR status. If successful, creates a
     * control file record and updates the control process as SUCCESS.
     *
     * @param jobExecution The Spring Batch job execution context
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info("Completing {} job execution with status: {}", contextHeader.getFilename(),
                jobExecution.getStatus());

        BatchStatus status = jobExecution.getStatus();
        if (status == BatchStatus.STOPPED || status == BatchStatus.FAILED) {
            handleJobFailed(jobExecution);
        } else {
            handleJobSuccess(jobExecution);
            log.info("{} job execution completed successfully", contextHeader.getFilename());
        }
    }

    private void checkForExistingProcessesForControlProcess(Long moduleId) {
        this.controlService.checkCtrlProcessAlreadyInExecution(moduleId);
    }

    /**
     * Validates and prepares financial cycles for the processing date. If no financial cycle exists
     * for the date, new cycles are created.
     */
    private void prepareCycles() {
        long cycleCount = finUtilsService.validateCycle(contextHeader.getProcDate());
        if (cycleCount == 0) {
            log.info("Financial cycle not found, preparing new cycles for date {}",
                    contextHeader.getProcDate());
            finUtilsService.prepareCycles(contextHeader.getProcDate());
        }
    }

    /**
     * Checks if the module has already been processed for the current date. If already processed,
     * sets the job status to STOPPING and exit status to FAILED.
     *
     * @param jobExecution The Spring Batch job execution context
     */
    private void checkProcessedModules(JobExecution jobExecution) {
        log.info("Checking if module has already been processed for date {}",
                contextHeader.getProcDate());
        int count = controlService.countProcessedModule(currentProcess.moduleId(),
                contextHeader.getProcDate());
        if (count != 0) {
            String errorMsg = String.format(
                    "Module has already been executed for date %s, stopping job",
                    contextHeader.getProcDate());
            log.error(errorMsg);
            jobExecution.setStatus(BatchStatus.STOPPING);
            jobExecution.setExitStatus(ExitStatus.FAILED);
            throw new JobAlreadyCompletedException(errorMsg);
        }
    }

    /**
     * Generates a user ID for job tracking and sets it in the context header. The format is:
     * jobName-currentDate
     *
     * @param jobExecution The Spring Batch job execution context
     */
    private void generateUserId(JobExecution jobExecution) {
        //define generic user id job_name + job_instance_id + LocalDate.now
        String sb = jobExecution.getJobInstance().getJobName() +
                "-" +
                DateUtils.getStringFromLocalDate(LocalDate.now(), DateUtils.YYYYMMDD);

        contextHeader.setUserId(sb);
    }

    private void createControlProcess(Long finModuleId, Long currFinPeriodId,
            JobExecution jobExecution) {
        log.info("Creating control process record");
        currentProcess = this.controlService.createControlProcess(finModuleId, currFinPeriodId,
                CtrlProcessStatusEnum.EXECUTING, "", contextHeader.getModuleShdes(),
                jobExecution.getJobInstance().getId(),
                "", "");
    }

    private void createControlFile() {
        log.info("Creating control file record");

        //TODO: Refactor this, probably better to move ctrl_process and ctrl_file inserts into a step
        // instead of staying here in the listener
        // Added this because there are jobs with multiple files
        Map<String, String> fileMap = contextCache.getFileMap();
        if (!fileMap.isEmpty()) {
            fileMap.keySet().forEach(key -> this.controlService.createControlFile(
                    currentProcess.controlProcessId(),
                    LocalDateTime.now(),
                    DateUtils.getLocalDateTimeFromString(contextHeader.getProcDate(),
                            DateUtils.YYYYMMDD),
                    fileMap.get(key), 0L, CtrlFileStatusEnum.EXECUTION.getCode(),
                    LocalDateTime.now(), LocalDateTime.now(), contextHeader.getUserId()));

            return;
        }

        currentControlFile = this.controlService.createControlFile(
                currentProcess.controlProcessId(),
                LocalDateTime.now(),
                DateUtils.getLocalDateTimeFromString(contextHeader.getProcDate(),
                        DateUtils.YYYYMMDD),
                contextHeader.getFilename(), 0L, CtrlFileStatusEnum.EXECUTION.getCode(),
                LocalDateTime.now(), LocalDateTime.now(), contextHeader.getUserId());
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

        //TODO: remove this asap - Danilo
        if(!contextCache.getFileMap().isEmpty()) {
            contextHeader.setFilename(contextCache.getFileMap().values().iterator().next());
        }

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

        if (currentProcess != null) {
            this.finUtilsService.rollbackFinDailyInvoice(jobExecution.getJobInstance().getId());

            log.error("Job failed, updating control process with ERROR status");
            this.controlService.updateControlProcess(currentProcess.controlProcessId(),
                    CtrlProcessStatusEnum.ERROR,
                    CtrlProcessFunctionalStatusEnum.ERROR,
                    "1", jobExecution.getFailureExceptions().getFirst().getMessage());
            this.controlService.updateControlFile(currentControlFile.fileControlId(),
                    CtrlFileStatusEnum.ERROR, 0L);
        } else {
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

        //TODO: Refactor this, probably better to move ctrl_process and ctrl_file inserts into a step
        // instead of staying here in the listener
        // Added this because there are jobs with multiple files
        Map<String, String> fileMap = contextCache.getFileMap();
        if (!fileMap.isEmpty()) {

            List<Long> byProcessId = this.controlService.findByProcessId(
                    currentProcess.controlProcessId());

            for (Long l : byProcessId) {
                controlService.updateControlFile(l,
                        CtrlFileStatusEnum.PROCESSED, writeCount);
            }

        } else {
            controlService.updateControlFile(currentControlFile.fileControlId(),
                    CtrlFileStatusEnum.PROCESSED, writeCount);
        }

        log.info("Updating control process with SUCCESS status");
        this.controlService.updateControlProcess(currentProcess.controlProcessId(),
                CtrlProcessStatusEnum.SUCCESS,
                CtrlProcessFunctionalStatusEnum.SUCCESS,
                "",
                "");
    }
}
