package pt.scml.fin.batch.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.model.dto.ControlProcessDTO;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

@Service
@Slf4j
public class JobPreparationService {

    private final ContextHeader contextHeader;

    private final ContextCache contextCache;

    private final ControlService controlService;

    private final FinUtilsService finUtilsService;

    private final ModuleValidationService moduleValidationService;

    private static final String CONFIG_KEY_CURRENT_PERIOD = "CURR_PERIOD_INV";

    private Long currFinPeriodId;
    private Long finModuleId;

    public JobPreparationService(ContextHeader contextHeader, ContextCache contextCache,
            ControlService controlService, FinUtilsService finUtilsService,
            ModuleValidationService moduleValidationService) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.controlService = controlService;
        this.finUtilsService = finUtilsService;
        this.moduleValidationService = moduleValidationService;
    }

    public void prepareJob(JobExecution jobExecution) {

        //jobParameterValidator.validateJobParameters();

        setupJobContext(jobExecution);

        moduleValidationService.validateModuleNotInExecution(contextHeader.getModuleId(), contextHeader.getFilename());

        log.info("Creating control process record");
        ControlProcessDTO controlProcess = this.controlService.createControlProcess(finModuleId,
                currFinPeriodId,
                CtrlProcessStatusEnum.EXECUTING, "", contextHeader.getModuleShdes(),
                jobExecution.getJobInstance().getId(),
                "", "");

        contextHeader.setControlProcessId(controlProcess.controlProcessId());

        createControlFile();
    }

    private void setupJobContext(JobExecution jobExecution) {

        Long moduleId = this.finUtilsService.getFinModuleId(contextHeader.getModuleShdes());
        String currFinPeriodIdStr = this.finUtilsService.getConfigValue(CONFIG_KEY_CURRENT_PERIOD);
        currFinPeriodId = Long.parseLong(currFinPeriodIdStr);
        finModuleId = moduleId;

        contextHeader.setCurrFinPeriodId(currFinPeriodId);
        contextHeader.setModuleId(moduleId);
        contextHeader.setJobExecutionId(jobExecution.getJobInstance().getId());

        contextHeader.setJobExecutionId(jobExecution.getJobInstance().getId());
        contextHeader.setUserId(generateUserId(jobExecution));
    }

    /**
     * Generates a user ID for job tracking and sets it in the context header. The format is:
     * jobName-currentDate
     *
     * @param jobExecution The Spring Batch job execution context
     */
    private String generateUserId(JobExecution jobExecution) {
        //define generic user id job_name + job_instance_id + LocalDate.now
        String sb = jobExecution.getJobInstance().getJobName() +
                "-" +
                DateUtils.getStringFromLocalDate(LocalDate.now(), DateUtils.YYYYMMDD);

        return sb;
    }

    private void createControlFile() {
        log.info("Creating control file record");

        this.controlService.createControlFile(
                contextHeader.getControlProcessId(),
                LocalDateTime.now(),
                DateUtils.getLocalDateTimeFromString(contextHeader.getProcDate(),
                        DateUtils.YYYYMMDD),
                contextHeader.getFilename(), 0L, CtrlFileStatusEnum.EXECUTION.getCode(),
                LocalDateTime.now(), LocalDateTime.now(), contextHeader.getUserId());
    }

}
