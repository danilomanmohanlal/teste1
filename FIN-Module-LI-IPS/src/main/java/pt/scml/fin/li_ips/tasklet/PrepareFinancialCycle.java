package pt.scml.fin.li_ips.tasklet;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.li_ips.JobParameters;
import pt.scml.fin.li_ips.utils.LIInvoicingParameterValidator;

@Slf4j
public class PrepareFinancialCycle implements Tasklet {

    private final LIInvoicingParameterValidator validator;
    private final JobParameters jobParameters;
    private final ContextCache contextCache;
    private final ContextHeader contextHeader;

    public PrepareFinancialCycle(LIInvoicingParameterValidator validator,  JobParameters jobParameters,
            ContextCache contextCache, ContextHeader contextHeader) {
        this.validator = validator;
        this.jobParameters = jobParameters;
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {


        log.info("######################################");
        contextHeader.setFilename(contextCache.getFileMap().get("IPS_ORDER_CONFIRMATION"));


        return RepeatStatus.FINISHED;
    }
}
