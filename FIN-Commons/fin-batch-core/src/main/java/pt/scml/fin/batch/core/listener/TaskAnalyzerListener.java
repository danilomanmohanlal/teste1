package pt.scml.fin.batch.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

/**
 * A custom listener that monitors the execution of a task in Spring Cloud Task. This listener is
 * responsible for checking the status of the associated job and propagating job execution details
 * to the task execution.
 * <p>
 * If a job fails during its execution, the listener sets the corresponding task execution's exit
 * code and message to reflect the job's failure status.
 * </p>
 */
@Slf4j
@Component
public class TaskAnalyzerListener implements TaskExecutionListener {

    private static final Integer FAILED_EXECUTION = 1;
    private final JobExplorer jobExplorer;

    @Autowired
    public TaskAnalyzerListener(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    @Override
    public void onTaskEnd(TaskExecution taskExecution) {

        log.info("on task end");
        JobExecution jobExecution = jobExplorer.getJobExecution(taskExecution.getExecutionId());
        if (jobExecution != null) {
            BatchStatus jobStatus = jobExecution.getStatus();
            if (BatchStatus.FAILED == jobStatus) {
                String exitDescription = jobExecution.getExitStatus().getExitDescription();
                taskExecution.setExitCode(FAILED_EXECUTION);
                taskExecution.setExitMessage(exitDescription);
            }
        }

        TaskExecutionListener.super.onTaskEnd(taskExecution);
    }

}
