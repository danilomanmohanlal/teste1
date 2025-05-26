package pt.scml.fin.batch.core.listener;

import static pt.scml.fin.batch.core.utils.FinUtils.moveFileTo;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;

@Component
public class MoveFileListener implements StepExecutionListener {


    private final ContextHeader contextHeader;
    private final ContextCache contextCache;

    public MoveFileListener(ContextHeader contextHeader, ContextCache contextCache) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (contextHeader.isJobHasAFile()) {
            moveFileTo(contextCache.getInputDirectory(),
                contextCache.getWorkDirectory(), contextHeader.getFilename());
        }

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        BatchStatus status = stepExecution.getStatus();
        if (status == BatchStatus.FAILED) {
            if (contextHeader.isJobHasAFile() && !contextHeader.isDuplicated()) {
                moveFileTo(contextCache.getWorkDirectory(),
                        contextCache.getErrorDirectory(), contextHeader.getFilename());
            }
            return ExitStatus.FAILED;
        }
        else {
            if (contextHeader.isJobHasAFile()) {
                moveFileTo(contextCache.getWorkDirectory(),
                    contextCache.getSuccessDirectory(), contextHeader.getFilename());
            }
        }

        return ExitStatus.COMPLETED;
    }
}
