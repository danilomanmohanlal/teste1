package listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import pt.scml.fin.batch.core.listener.ChunkAnalyzerListener;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChunkAnalyzerListenerTest {

    private JobOperator jobOperator;
    private ChunkAnalyzerListener listener;

    @BeforeEach
    void setUp() {
        jobOperator = mock(JobOperator.class);
        listener = new ChunkAnalyzerListener(jobOperator);
    }

    @Test
    void testAfterChunkError_callsJobOperatorStop() throws Exception {
        ChunkContext chunkContext = mock(ChunkContext.class);
        StepContext stepContext = mock(StepContext.class);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getJobInstanceId()).thenReturn(123L);

        listener.afterChunkError(chunkContext);

        verify(jobOperator).stop(123L);
    }

    @Test
    void testAfterChunkError_handlesNoSuchJobExecutionException() throws Exception {
        ChunkContext chunkContext = mock(ChunkContext.class);
        StepContext stepContext = mock(StepContext.class);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getJobInstanceId()).thenReturn(456L);

        doThrow(new NoSuchJobExecutionException("Job not found"))
            .when(jobOperator).stop(456L);

        listener.afterChunkError(chunkContext);

        verify(jobOperator).stop(456L);
    }

    @Test
    void testAfterChunkError_handlesJobExecutionNotRunningException() throws Exception {
        ChunkContext chunkContext = mock(ChunkContext.class);
        StepContext stepContext = mock(StepContext.class);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getJobInstanceId()).thenReturn(789L);

        doThrow(new JobExecutionNotRunningException("Job not running"))
            .when(jobOperator).stop(789L);

        listener.afterChunkError(chunkContext);

        verify(jobOperator).stop(789L);
    }
}
