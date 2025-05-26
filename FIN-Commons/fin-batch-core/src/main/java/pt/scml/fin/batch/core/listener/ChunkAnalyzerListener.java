package pt.scml.fin.batch.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Custom {@link ChunkListener} that monitors the processing of chunks during Spring Batch job
 * execution.
 * <p>
 * This listener stops the job execution immediately when a chunk error occurs, which can be helpful
 * for ensuring that errors are addressed promptly without proceeding with further processing.
 * </p>
 * It listens for chunk error events and performs a job stop operation by invoking the
 * {@link JobOperator}.
 */
@Component
@Slf4j
public class ChunkAnalyzerListener implements ChunkListener {

    private final JobOperator jobOperator;

    @Autowired
    public ChunkAnalyzerListener(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Error processing chunk, stopping step and job execution...");

        try {
            jobOperator.stop(context.getStepContext().getJobInstanceId());
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            log.error("No JobExecution found for id: {}",
                context.getStepContext().getJobInstanceId());
        }

        ChunkListener.super.afterChunkError(context);
    }
}
