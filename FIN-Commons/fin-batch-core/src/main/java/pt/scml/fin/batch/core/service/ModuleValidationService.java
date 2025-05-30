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
    private final ControlProcessRepository controlProcessRepository;

    public ModuleValidationService(ControlService controlService,
            ControlProcessRepository controlProcessRepository) {
        this.controlService = controlService;
        this.controlProcessRepository = controlProcessRepository;
    }

    public void validateModuleNotInExecution(Long moduleId, String filename) {
        if (isModuleAlreadyExecuting(moduleId)) {
            throw new JobWithModuleIdInExecutionException("Module already executing: " + moduleId);
        }

        isModuleFilenameAlreadyExecuting(moduleId, filename);
    }

    private boolean isModuleAlreadyExecuting(Long moduleId) {

        log.info("Checking if there is already processing in execution");
        ControlProcess processInExecution = this.controlProcessRepository.findProcessInExecutionByModuleId(
                CtrlProcessStatusEnum.EXECUTING.getCode(), moduleId);
        if (processInExecution != null)
            return true;

        return false;
    }

    private void isModuleFilenameAlreadyExecuting(Long moduleId, String filename) {
        controlService.checkCtrlFileAlreadyExecuted(filename, moduleId);
    }


}