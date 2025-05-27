package pt.scml.fin.batch.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.exceptions.JobAlreadyCompletedException;
import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;
import pt.scml.fin.model.entities.ControlProcess;
import pt.scml.fin.model.repo.ControlProcessRepository;

@Service
@Slf4j
public class ModuleValidationService {
    
    private final ControlService controlService;
    private ControlProcessRepository controlProcessRepository;

    public ModuleValidationService(ControlService controlService) {
        this.controlService = controlService;
    }

    public void validateModuleNotInExecution(Long moduleId) {
        if (isModuleAlreadyExecuting(moduleId)) {
            throw new JobWithModuleIdInExecutionException("Module already executing: " + moduleId);
        }
    }

    private boolean isModuleAlreadyExecuting(Long moduleId) {

        log.info("Checking if there is already processing in execution");
        ControlProcess processInExecution = this.controlProcessRepository.findProcessInExecutionByModuleId(
                CtrlProcessStatusEnum.EXECUTING.getCode(), moduleId);
        if (processInExecution != null)
            return true;

        return false;
    }

    public void validateModuleNotAlreadyProcessed(Long moduleId, String procDate) {
        //if (isModuleAlreadyProcessed(moduleId, procDate)) {
          //  throw new JobAlreadyCompletedException("Module already processed for date: " + procDate);
        //}
    }
}