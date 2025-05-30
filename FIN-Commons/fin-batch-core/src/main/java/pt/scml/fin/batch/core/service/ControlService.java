package pt.scml.fin.batch.core.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;
import pt.scml.fin.model.dto.ControlFileDTO;
import pt.scml.fin.model.dto.ControlProcessDTO;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessFunctionalStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;
import pt.scml.fin.model.entities.ControlFile;
import pt.scml.fin.model.entities.ControlProcess;
import pt.scml.fin.model.repo.ControlFileRepository;
import pt.scml.fin.model.repo.ControlProcessRepository;

/**
 * Service class responsible for managing control processes and control files. Provides methods to
 * create and update control processes, and to create control files.
 */
@Slf4j
@Service
public class ControlService {

    private final ControlProcessRepository controlProcessRepository;
    private final ControlFileRepository controlFileRepository;

    public ControlService(ControlProcessRepository controlProcessRepository,
        ControlFileRepository controlFileRepository) {
        this.controlProcessRepository = controlProcessRepository;
        this.controlFileRepository = controlFileRepository;
    }

    /**
     * Creates a new control process entry and persists it.
     *
     * @param moduleId    the module ID associated with the process
     * @param finPeriodId the financial period ID
     * @param jobParams   parameters used for the job
     * @param moduleShdes the name of the job
     * @return the created {@link ControlProcessDTO} with persisted data
     */
    public ControlProcessDTO createControlProcess(Long moduleId, Long finPeriodId,
        CtrlProcessStatusEnum processStatusEnum, String jobParams, String moduleShdes,
        Long jobInstanceId, String errorCode, String errorMessage) {
        log.info("Creating control process - moduleId: {}, finPeriodId: {}, moduleShdes: {}",
            moduleId, finPeriodId, moduleShdes);

        ControlProcessDTO controlProcessDTO = ControlProcessDTO.builder()
            .moduleId(moduleId)
            .finPeriodId(finPeriodId)
            .processStatus(processStatusEnum.getCode())
            .pid(String.valueOf(ProcessHandle.current().pid()))
            .userId("FIN_APP")
            .calledParams(jobParams)
            .data1(moduleShdes)
            .startDate(LocalDateTime.now())
            .jobInstanceId(jobInstanceId)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();

        ControlProcess controlProcess = controlProcessRepository.save(controlProcessDTO.toEntity());
        ControlProcessDTO result = controlProcess.toDTO();

        log.info("Control process created successfully - ID: {}", result.controlProcessId());

        return result;
    }

    /**
     * Creates a new failed control process entry and persists it.
     *
     * @param moduleId    the module ID associated with the process
     * @param finPeriodId the financial period ID
     * @param jobParams   parameters used for the job
     * @param moduleShdes the name of the job
     * @return the created {@link ControlProcessDTO} with persisted data
     */
    public ControlProcessDTO createControlProcessError(Long moduleId, Long finPeriodId,
        String jobParams, String moduleShdes,
        Long jobInstanceId, String errorCode, String errorMessage) {
        log.info("Creating control process - moduleId: {}, finPeriodId: {}, moduleShdes: {}",
            moduleId, finPeriodId, moduleShdes);

        ControlProcessDTO controlProcessDTO = ControlProcessDTO.builder()
            .moduleId(moduleId)
            .finPeriodId(finPeriodId)
            .processStatus(CtrlProcessStatusEnum.ERROR.getCode())
            .functionalStatus(CtrlProcessFunctionalStatusEnum.ERROR.getCode())
            .pid(String.valueOf(ProcessHandle.current().pid()))
            .userId("FIN_APP")
            .calledParams(jobParams)
            .data1(moduleShdes)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .jobInstanceId(jobInstanceId)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .build();

        ControlProcess controlProcess = controlProcessRepository.save(controlProcessDTO.toEntity());
        ControlProcessDTO result = controlProcess.toDTO();

        log.info("Control process created successfully - ID: {}", result.controlProcessId());

        return result;
    }

    /**
     * Updates the status and functional status of an existing control process.
     *
     * @param controlProcessId the ID of the control process to update
     * @param processStatus    the new status of the process
     * @param functionalStatus the new functional status of the process
     */
    public void updateControlProcess(Long controlProcessId, CtrlProcessStatusEnum processStatus,
        CtrlProcessFunctionalStatusEnum functionalStatus, String errorCode, String errorMessage) {
        log.info("Updating control process - ID: {}, status: {}, functionalStatus: {}",
            controlProcessId, processStatus, functionalStatus);

        ControlProcess controlProcess = controlProcessRepository.findById(controlProcessId).get();
        controlProcess.setProcessStatus(processStatus.getCode());
        controlProcess.setFunctionalStatus(functionalStatus.getCode());
        controlProcess.setEndDate(LocalDateTime.now());
        controlProcess.setErrorCode(errorCode);
        controlProcess.setErrorMessage(errorMessage);
        controlProcessRepository.save(controlProcess);

        log.info("Control process updated successfully - ID: {}", controlProcessId);

    }

    /**
     * Updates the status and functional status of an existing control file.
     *
     * @param controlFileId the ID of the control file to update
     * @param status        the new status of the process
     * @param numRecords    the new functional status of the process
     */
    public void updateControlFile(Long controlFileId, CtrlFileStatusEnum status,
        Long numRecords) {
        log.info("Updating control file - ID: {}, status: {}, numRecords: {}",
            controlFileId, status, numRecords);

        Optional<ControlFile> ctrlFileOptional = controlFileRepository.findById(controlFileId);
        if (ctrlFileOptional.isPresent()) {
            ControlFile controlFile = ctrlFileOptional.get();
            controlFile.setStatus(status.getCode());
            controlFile.setNumRecTotal(numRecords);
            controlFileRepository.save(controlFile);
            log.info("Control file updated successfully - ID: {}", controlFileId);
        } else {
            log.error("Control file not found - ID: {}", controlFileId);
        }
    }

    /**
     * Creates a new control file and associates it with a control process.
     *
     * @param ctrlProcessId   the ID of the control process to associate
     * @param creationDate    the date the control file was created
     * @param processedDate   the date the file was processed
     * @param filename        the name of the file
     * @param numRecordsTotal total number of records in the file
     * @param status          the status of the file
     * @param dataStartDate   the start date of data contained in the file
     * @param dataEndDate     the end date of data contained in the file
     * @param lastUserId      ID of the last user who modified or handled the file
     */
    public ControlFileDTO createControlFile(Long ctrlProcessId, LocalDateTime creationDate,
        LocalDateTime processedDate,
        String filename, Long numRecordsTotal, String status, LocalDateTime dataStartDate,
        LocalDateTime dataEndDate,
        String lastUserId) {

        log.info("Creating control file - processId: {}, filename: {}, status: {}", ctrlProcessId,
            filename, status);

        ControlProcess controlProcess = controlProcessRepository.findById(ctrlProcessId).get();

        ControlFileDTO controlFileDTO = ControlFileDTO.builder()
            .processId(controlProcess.toDTO())
            .creationDate(creationDate)
            .processedDate(processedDate)
            .filename(filename)
            .numRecTotal(numRecordsTotal)
            .status(status)
            .dataStartDate(dataStartDate)
            .dataEndDate(dataEndDate)
            .lastUserId(lastUserId)
            .build();

        ControlFile savedEntity = controlFileRepository.save(controlFileDTO.toEntity());

        log.info("Control file created successfully for process ID: {}", ctrlProcessId);
        return savedEntity.toDTO();


    }

    public int countProcessedModule(Long moduleId, String procDate) {
        log.info("Counting processed modules - moduleId: {}, procDate: {}", moduleId, procDate);

        int count = controlProcessRepository.countProcessedModule(moduleId,
            CtrlProcessStatusEnum.SUCCESS.getCode(),
            CtrlProcessFunctionalStatusEnum.SUCCESS.getCode(), procDate);

        log.info("Processed module count result: {} for moduleId: {}", count, moduleId);

        return count;
    }

    public void checkCtrlProcessAlreadyInExecution(Long moduleId) {
        log.info("Checking if there is already processing in execution");
        ControlProcess processInExecution = this.controlProcessRepository.findProcessInExecutionByModuleId(
            CtrlProcessStatusEnum.EXECUTING.getCode(), moduleId);
        if (processInExecution != null) {
            throw new JobWithModuleIdInExecutionException("This module is already executing");
        }
    }

    public void checkCtrlFileAlreadyExecuted(String filename, Long moduleId) {
        log.info("Checking if there is already executed");
        int processInExecution = this.controlFileRepository.countByFileNameAndControlProcessId(
            filename, moduleId);
        if (processInExecution != 0) {
            String errorMsg = String.format("This module with this %s is already executed",
                filename);
            throw new JobWithModuleIdInExecutionException(errorMsg);
        }
    }

    public boolean isFileAlreadyProcessed(String fileName) {
        return this.controlFileRepository.existsByFilenameAndStatus(fileName, "P");
    }

    public List<Long> findByProcessId(Long processId) {
        return this.controlFileRepository.findByProcessIdControlProcessId(processId);
    }
}

