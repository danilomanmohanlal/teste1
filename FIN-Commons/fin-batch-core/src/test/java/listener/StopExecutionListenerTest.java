package listener;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import pt.scml.fin.batch.core.listener.StepExceptionListener;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StepExceptionListenerTest {

    private StepExceptionListener listener;
    private StepExecution stepExecution;
    private JobExecution jobExecution;

    @BeforeEach
    void setUp() {
        listener = new StepExceptionListener();
        stepExecution = mock(StepExecution.class);
        jobExecution = mock(JobExecution.class);
    }

    @Test
    void testAfterStep_StatusCompleted() {
        when(stepExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(stepExecution.getFailureExceptions()).thenReturn(Collections.emptyList());

        ExitStatus exitStatus = listener.afterStep(stepExecution);

        assertNull(exitStatus, "ExitStatus should be null if step completed successfully");
        verify(stepExecution, never()).getJobExecution();
    }

    @Test
    void testAfterStep_StatusFailed_WithExceptions() {
        Throwable ex1 = new RuntimeException("Erro 1");
        Throwable ex2 = new IllegalStateException("Erro 2");

        List<Throwable> exceptionList = List.of(ex1, ex2);

        when(stepExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(stepExecution.getFailureExceptions()).thenReturn(exceptionList);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);

        ExitStatus exitStatus = listener.afterStep(stepExecution);

        assertNull(exitStatus, "ExitStatus should still be null even after failure");
        verify(jobExecution).addFailureException(ex1);
        verify(jobExecution).addFailureException(ex2);
    }

    @Test
    void testAfterStep_StatusFailed_WithoutExceptions() {
        when(stepExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(stepExecution.getFailureExceptions()).thenReturn(Collections.emptyList());

        ExitStatus exitStatus = listener.afterStep(stepExecution);

        assertNull(exitStatus, "ExitStatus should be null if no exceptions are present");
        verify(stepExecution, never()).getJobExecution();
    }
}
