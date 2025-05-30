package pt.scml.fin.batch.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.service.FinUtilsService;

@Slf4j
@Component
public class JobFinancialCycleListener implements JobExecutionListener {

    private final FinUtilsService finUtilsService;
    private final ContextHeader contextHeader;

    public JobFinancialCycleListener(FinUtilsService finUtilsService, ContextHeader contextHeader) {
        this.finUtilsService = finUtilsService;
        this.contextHeader = contextHeader;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {

        prepareCycles(contextHeader.getProcDate());
    }

    /**
     * Validates and prepares financial cycles for the processing date. If no financial cycle exists
     * for the date, new cycles are created.
     */
    private void prepareCycles(String procDate) {
        long cycleCount = finUtilsService.validateCycle(procDate);
        if (cycleCount == 0) {
            log.info("Financial cycle not found, preparing new cycles for date {}",
                    contextHeader.getProcDate());
            finUtilsService.prepareCycles(contextHeader.getProcDate());
        }
    }
}
