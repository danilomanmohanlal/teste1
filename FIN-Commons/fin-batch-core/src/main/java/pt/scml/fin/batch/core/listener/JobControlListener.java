package pt.scml.fin.batch.core.listener;

import static pt.scml.fin.batch.core.utils.FinUtils.moveFileTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
import pt.scml.fin.batch.core.service.JobCompletionService;
import pt.scml.fin.batch.core.service.JobPreparationService;
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

    private final JobPreparationService jobPreparationService;
    private final JobCompletionService jobCompletionService;

    @Autowired
    public JobControlListener(ContextHeader contextHeader, ContextCache contextCache,
            ControlService controlService,
            FinUtilsService finUtilsService, JobPreparationService jobPreparationService,
            JobCompletionService jobCompletionService) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.controlService = controlService;
        this.finUtilsService = finUtilsService;
        this.jobPreparationService = jobPreparationService;
        this.jobCompletionService = jobCompletionService;
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

        if (contextHeader.isHasFileError()) {
            throw new RuntimeException("FICHEIRO NOT FOUND");
        }

        log.info("Validate if there is already an job in execution or executed for {}",
                DateUtils.getStringFromLocalDate(LocalDate.now(), DateUtils.YYYYMMDD));

        jobPreparationService.prepareJob(jobExecution);

    }

//    private void checkForExecutedProcessesForControlFile() {
//        this.controlService.checkCtrlFileAlreadyExecuted(contextHeader.getFilename(),
//                contextHeader.getModuleId());
//    }

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

        jobCompletionService.completeJob(jobExecution);
    }

    private void checkForExistingProcessesForControlProcess(Long moduleId) {
        this.controlService.checkCtrlProcessAlreadyInExecution(moduleId);
    }

//    /**
//     * Validates and prepares financial cycles for the processing date. If no financial cycle exists
//     * for the date, new cycles are created.
//     */
//    private void prepareCycles() {
//        long cycleCount = finUtilsService.validateCycle(contextHeader.getProcDate());
//        if (cycleCount == 0) {
//            log.info("Financial cycle not found, preparing new cycles for date {}",
//                    contextHeader.getProcDate());
//            finUtilsService.prepareCycles(contextHeader.getProcDate());
//        }
//    }

    /**
     * Checks if the module has already been processed for the current date. If already processed,
     * sets the job status to STOPPING and exit status to FAILED.
     *
     * @param jobExecution The Spring Batch job execution context
     */
//    private void checkProcessedModules(JobExecution jobExecution) {
//        log.info("Checking if module has already been processed for date {}",
//                contextHeader.getProcDate());
//        int count = controlService.countProcessedModule(currentProcess.moduleId(),
//                contextHeader.getProcDate());
//        if (count != 0) {
//            String errorMsg = String.format(
//                    "Module has already been executed for date %s, stopping job",
//                    contextHeader.getProcDate());
//            log.error(errorMsg);
//            jobExecution.setStatus(BatchStatus.STOPPING);
//            jobExecution.setExitStatus(ExitStatus.FAILED);
//            throw new JobAlreadyCompletedException(errorMsg);
//        }
//    }



//    private void createControlProcess(Long finModuleId, Long currFinPeriodId,
//            JobExecution jobExecution) {
//        log.info("Creating control process record");
//        currentProcess = this.controlService.createControlProcess(finModuleId, currFinPeriodId,
//                CtrlProcessStatusEnum.EXECUTING, "", contextHeader.getModuleShdes(),
//                jobExecution.getJobInstance().getId(),
//                "", "");
//    }




}
