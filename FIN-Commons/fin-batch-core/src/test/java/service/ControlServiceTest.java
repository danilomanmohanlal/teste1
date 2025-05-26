//package service;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;
//import pt.scml.fin.batch.core.service.ControlService;
//import pt.scml.fin.model.dto.ControlProcessDTO;
//import pt.scml.fin.model.dto.enums.CtrlProcessFunctionalStatusEnum;
//import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;
//import pt.scml.fin.model.entities.ControlProcess;
//import pt.scml.fin.model.repo.ControlFileRepository;
//import pt.scml.fin.model.repo.ControlProcessRepository;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ControlServiceTest {
//
//    @Mock
//    private ControlProcessRepository controlProcessRepository;
//
//    @Mock
//    private ControlFileRepository controlFileRepository;
//
//    @InjectMocks
//    private ControlService controlService;
//
//    @Test
//    void testCreateControlProcess_success() {
//        ControlProcessDTO dto = ControlProcessDTO.builder()
//            .moduleId(1L)
//            .finPeriodId(2024L)
//            .processStatus("SUCCESS")
//            .pid("123")
//            .userId("FIN_APP")
//            .calledParams("param")
//            .data1("JOB")
//            .startDate(LocalDateTime.now())
//            .jobInstanceId(1L)
//            .errorCode(null)
//            .errorMessage(null)
//            .build();
//
//        ControlProcess entity = dto.toEntity();
//        entity.setControlProcessId(100L);
//        when(controlProcessRepository.save(any())).thenReturn(entity);
//
//        ControlProcessDTO result = controlService.createControlProcess(
//            1L, 2024L, CtrlProcessStatusEnum.SUCCESS,
//            "param", "JOB", 1L, null, null);
//
//        assertNotNull(result);
//        assertEquals(1L, result.moduleId());
//    }
//
//    @Test
//    void testUpdateControlProcess_success() {
//        ControlProcess entity = new ControlProcess();
//        entity.setControlProcessId(10L);
//        when(controlProcessRepository.findById(10L)).thenReturn(Optional.of(entity));
//
//        controlService.updateControlProcess(10L, CtrlProcessStatusEnum.SUCCESS,
//            CtrlProcessFunctionalStatusEnum.SUCCESS,
//            "",
//            "");
//
//        verify(controlProcessRepository).save(any(ControlProcess.class));
//    }
//
//    @Test
//    void testCreateControlProcessError_success() {
//        ControlProcessDTO dto = ControlProcessDTO.builder()
//            .moduleId(2L)
//            .finPeriodId(2025L)
//            .processStatus(CtrlProcessStatusEnum.ERROR.getCode())
//            .functionalStatus(CtrlProcessFunctionalStatusEnum.ERROR.getCode())
//            .pid("123")
//            .userId("FIN_APP")
//            .calledParams("param")
//            .data1("JOB_ERR")
//            .startDate(LocalDateTime.now())
//            .endDate(LocalDateTime.now())
//            .jobInstanceId(2L)
//            .errorCode("1")
//            .errorMessage("Something went wrong")
//            .build();
//
//        ControlProcess entity = dto.toEntity();
//        entity.setControlProcessId(200L);
//        when(controlProcessRepository.save(any())).thenReturn(entity);
//
//        ControlProcessDTO result = controlService.createControlProcessError(
//            2L, 2025L, "param", "JOB_ERR", 2L, "E001", "Something went wrong");
//
//        assertEquals(CtrlProcessStatusEnum.ERROR.getCode(), result.processStatus());
//        assertEquals("1", result.errorCode());
//    }
//
//    @Test
//    void testCreateControlFile_success() {
//        ControlProcess controlProcess = new ControlProcess();
//        controlProcess.setControlProcessId(1L);
//        when(controlProcessRepository.findById(1L)).thenReturn(Optional.of(controlProcess));
//
//        controlService.createControlFile(1L,
//            LocalDateTime.now(), LocalDateTime.now(),
//            "test.csv", 100L, "PROCESSED",
//            LocalDateTime.now(), LocalDateTime.now(),
//            "FIN_APP");
//
//        verify(controlFileRepository).save(any());
//    }
//
//    @Test
//    void testCountProcessedModule_returnsCount() {
//        when(controlProcessRepository.countProcessedModule(anyLong(), anyString(), anyString(),
//            anyString()))
//            .thenReturn(5);
//
//        int result = controlService.countProcessedModule(1L, "2025-01-01");
//
//        assertEquals(5, result);
//    }
//
//    @Test
//    void testCheckCtrlProcessAlreadyInExecution_throwsException() {
//        ControlProcess executingProcess = new ControlProcess();
//        when(controlProcessRepository.findProcessInExecutionByModuleId(
//            CtrlProcessStatusEnum.EXECUTING.getCode(), 1L))
//            .thenReturn(executingProcess);
//
//        assertThrows(JobWithModuleIdInExecutionException.class,
//            () -> controlService.checkCtrlProcessAlreadyInExecution(1L));
//    }
//
//    @Test
//    void testCheckCtrlProcessAlreadyInExecution_noException() {
//        when(controlProcessRepository.findProcessInExecutionByModuleId(
//            CtrlProcessStatusEnum.EXECUTING.getCode(), 1L))
//            .thenReturn(null);
//
//        assertDoesNotThrow(() -> controlService.checkCtrlProcessAlreadyInExecution(1L));
//    }
//}
