package pt.scml.fin.batch.core.listener;

import java.util.List;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Custom {@link StepExecutionListener} that handles exceptions occurring during a step execution in
 * a Spring Batch job.
 * <p>
 * This listener monitors the step execution and, in case of failure, propagates any failure
 * exceptions to the job execution to ensure they are properly captured and reported at the job
 * level.
 * </p>
 */
@Component
public class StepExceptionListener implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        if (stepExecution.getStatus() == BatchStatus.FAILED) {

            List<Throwable> failureExceptions = stepExecution.getFailureExceptions();
            if (!failureExceptions.isEmpty()) {

                //propagar para job execution e apanhar a excepção no job listener
                failureExceptions.forEach(
                    e -> stepExecution.getJobExecution().addFailureException(e));
            }
        }

        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
