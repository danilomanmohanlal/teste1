package pt.scml.fin.batch.core.service;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

@Service
@Slf4j
public class JobPreparationService {


    private final ModuleValidationService moduleValidationService;
    private final FinancialCycleService financialCycleService;
    private final ControlService controlService;
    private final ContextHeader contextHeader;
    private static final String CONFIG_KEY_CURRENT_PERIOD = "CURR_PERIOD_INV";
    private final FinUtilsService finUtilsService;

    Long finModuleId = 0l;
    Long currFinPeriodId = 0l;


    public JobPreparationService(
            ModuleValidationService moduleValidationService,
            FinancialCycleService financialCycleService,
            ControlService controlService, ContextHeader contextHeader,
            FinUtilsService finUtilsService) {
        this.moduleValidationService = moduleValidationService;
        this.financialCycleService = financialCycleService;
        this.controlService = controlService;
        this.contextHeader = contextHeader;
        this.finUtilsService = finUtilsService;
    }

    public void prepareJob(JobExecution jobExecution) {
        // 1. Validate parameters
        //jobParameterValidator.validateJobParameters();
        
        // 2. Setup job context
        setupJobContext(jobExecution);
        
        // 3. Validate module not already running
        moduleValidationService.validateModuleNotInExecution(contextHeader.getModuleId());
        
        // 4. Prepare financial cycles
        financialCycleService.ensureFinancialCyclesExist(contextHeader.getProcDate());
        
        // 5. Create control records
        log.info("Creating control process record");
        this.controlService.createControlProcess(finModuleId, currFinPeriodId,
                CtrlProcessStatusEnum.EXECUTING, "", contextHeader.getModuleShdes(),
                jobExecution.getJobInstance().getId(),
                "", "");
    }

    private void setupJobContext(JobExecution jobExecution) {
        // Clean, focused method for context setup

        Long moduleId = this.finUtilsService.getFinModuleId(contextHeader.getModuleShdes());
        String currFinPeriodIdStr = this.finUtilsService.getConfigValue(CONFIG_KEY_CURRENT_PERIOD);
        currFinPeriodId = Long.parseLong(currFinPeriodIdStr);
        finModuleId = moduleId;

        contextHeader.setCurrFinPeriodId(currFinPeriodId);
        contextHeader.setModuleId(moduleId);
        contextHeader.setJobExecutionId(jobExecution.getJobInstance().getId());

        contextHeader.setJobExecutionId(jobExecution.getJobInstance().getId());
        contextHeader.setUserId(generateUserId(jobExecution));
        // ... other context setup
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
}