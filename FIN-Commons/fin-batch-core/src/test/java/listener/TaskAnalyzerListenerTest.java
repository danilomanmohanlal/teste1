package listener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.cloud.task.repository.TaskExecution;
import pt.scml.fin.batch.core.listener.TaskAnalyzerListener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskAnalyzerListenerTest {

    private JobExplorer jobExplorer;
    private TaskAnalyzerListener taskAnalyzerListener;

    @BeforeEach
    void setUp() {
        jobExplorer = mock(JobExplorer.class);
        taskAnalyzerListener = new TaskAnalyzerListener(jobExplorer);
    }

    @Test
    void testOnTaskEnd_whenJobExecutionIsFailed_thenSetExitCodeAndMessage() {
        // given
        long executionId = 123L;
        TaskExecution taskExecution = new TaskExecution();

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(jobExecution.getExitStatus()).thenReturn(
            new ExitStatus("FAILED", "Some failure message"));
        when(jobExplorer.getJobExecution(taskExecution.getExecutionId())).thenReturn(jobExecution);

        // when
        taskAnalyzerListener.onTaskEnd(taskExecution);

        // then
        assertEquals(1, taskExecution.getExitCode());
        assertEquals("Some failure message", taskExecution.getExitMessage());
    }

    @Test
    void testOnTaskEnd_whenJobExecutionIsCompleted_thenDoNothing() {
        // given
        long executionId = 123L;
        TaskExecution taskExecution = new TaskExecution();

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(jobExplorer.getJobExecution(executionId)).thenReturn(jobExecution);

        // when
        taskAnalyzerListener.onTaskEnd(taskExecution);

        // then
        assertEquals(null, taskExecution.getExitCode());
        assertEquals(null, taskExecution.getExitMessage());
    }

    @Test
    void testOnTaskEnd_whenJobExecutionIsNull_thenDoNothing() {
        // given
        long executionId = 123L;
        TaskExecution taskExecution = new TaskExecution();
        when(jobExplorer.getJobExecution(executionId)).thenReturn(null);

        // when
        taskAnalyzerListener.onTaskEnd(taskExecution);

        // then
        assertEquals(null, taskExecution.getExitCode());
        assertEquals(null, taskExecution.getExitMessage());
    }
}

