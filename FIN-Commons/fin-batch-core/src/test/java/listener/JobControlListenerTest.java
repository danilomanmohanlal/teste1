package listener;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.exceptions.JobAlreadyCompletedException;
import pt.scml.fin.batch.core.listener.JobControlListener;
import pt.scml.fin.batch.core.service.ControlService;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.model.dto.ControlProcessDTO;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessFunctionalStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JobControlListenerTest {

    //TODO fix tests

    private final String moduleShdes = "MOD_SH";
    @Mock
    private ContextHeader contextHeader;
    @Mock
    private ControlService controlService;
    @Mock
    private FinUtilsService finUtilsService;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private JobInstance jobInstance;
    @InjectMocks
    private JobControlListener listener;

    //@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(contextHeader.getModuleShdes()).thenReturn(moduleShdes);
        when(contextHeader.getProcDate())
            .thenReturn(
                DateUtils.getStringFromLocalDateTime(LocalDateTime.now(), DateUtils.YYYYMMDD));

        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getId()).thenReturn(1L);
        when(jobInstance.getJobName()).thenReturn("testJob");

        when(finUtilsService.getFinModuleId(moduleShdes)).thenReturn(100L);
        when(finUtilsService.getConfigValue("CURR_PERIOD_INV")).thenReturn("202401");
        when(controlService.countProcessedModule(anyLong(), any())).thenReturn(0);
        when(finUtilsService.validateCycle(anyString())).thenReturn(1L);
    }

    //@Test
    void testBeforeJob_SuccessfulExecution() {
        ControlProcessDTO dto =
            ControlProcessDTO.builder()
                .controlProcessId(1L)
                .moduleId(100L)
                .finPeriodId(202401L)
                .jobInstanceId(1L)
                .build();

        when(controlService.createControlProcess(
            any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(dto);

        listener.beforeJob(jobExecution);

        verify(contextHeader).setCurrFinPeriodId(202401L);
        verify(contextHeader).setModuleId(100L);
        verify(contextHeader).setJobExecutionId(1L);
        verify(contextHeader).setControlProcessId(1L);
        verify(contextHeader).setUserId(contains("testJob"));
    }

    //@Test
    void testBeforeJob_ModuleAlreadyProcessed_ThrowsException() {
        ControlProcessDTO dto = ControlProcessDTO.builder().controlProcessId(1L).moduleId(100L)
            .build();

        when(controlService.createControlProcess(
            any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(dto);
        when(controlService.countProcessedModule(anyLong(), any())).thenReturn(1);

        assertThrows(JobAlreadyCompletedException.class, () -> listener.beforeJob(jobExecution));
    }

    //@Test
    void testAfterJob_SuccessfulExecution() {
        StepExecution stepExecution = mock(StepExecution.class);
        when(stepExecution.getWriteCount()).thenReturn(5L);
        when(jobExecution.getStepExecutions()).thenReturn(Collections.singleton(stepExecution));
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        ControlProcessDTO dto = ControlProcessDTO.builder().controlProcessId(1L).moduleId(100L)
            .build();
        //listener = new JobControlListener(contextHeader, controlService, finUtilsService);
        setPrivateCurrentProcess(dto);

        listener.afterJob(jobExecution);

        verify(controlService)
            .createControlFile(
                eq(1L),
                any(),
                any(),
                any(),
                eq(5L),
                eq(CtrlFileStatusEnum.PROCESSED.getCode()),
                any(),
                any(),
                any());

        verify(controlService)
            .updateControlProcess(
                1L, CtrlProcessStatusEnum.SUCCESS, CtrlProcessFunctionalStatusEnum.SUCCESS, "", "");
    }

    //@Test
    void testAfterJob_FailedExecution_WithControlProcess() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);

        Exception rootCause = new RuntimeException("job failed execution");
        List<Throwable> failureExceptions = List.of(rootCause);
        when(jobExecution.getFailureExceptions()).thenReturn(failureExceptions);

        ControlProcessDTO dto = ControlProcessDTO.builder().controlProcessId(1L).moduleId(100L)
            .build();
        //listener = new JobControlListener(contextHeader, controlService, finUtilsService);
        setPrivateCurrentProcess(dto);

        listener.afterJob(jobExecution);

        verify(finUtilsService).rollbackFinDailyInvoice(any());
        verify(controlService)
            .updateControlProcess(
                1L, CtrlProcessStatusEnum.ERROR, CtrlProcessFunctionalStatusEnum.ERROR, "1",
                "job failed execution");
    }

    //@Test
    void testAfterJob_FailedExecution_WithoutControlProcess() {
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(contextHeader.getModuleId()).thenReturn(100L);
        when(contextHeader.getCurrFinPeriodId()).thenReturn(202401L);

        //listener.afterJob(jobExecution);

        verify(controlService)
            .createControlProcessError(
                eq(100L),
                eq(202401L),
                any(),
                eq(moduleShdes),
                eq(1L),
                eq("2"),
                contains("already in execution"));
    }

    private void setPrivateCurrentProcess(ControlProcessDTO dto) {
        try {
            java.lang.reflect.Field field = JobControlListener.class.getDeclaredField(
                "currentProcess");
            field.setAccessible(true);
            field.set(listener, dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
