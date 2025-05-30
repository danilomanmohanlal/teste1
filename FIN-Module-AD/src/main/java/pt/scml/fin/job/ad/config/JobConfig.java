package pt.scml.fin.job.ad.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.PlatformTransactionManager;
import pt.scml.fin.batch.core.config.AbstractJobConfig;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.listener.StepMoveFileListener;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.writer.FinDailyInvoiceWriter;
import pt.scml.fin.job.ad.JobParameters;
import pt.scml.fin.job.ad.dto.ADDataDTO;
import pt.scml.fin.job.ad.processor.ADPayedPrizesProcessor;
import pt.scml.fin.job.ad.reader.ADLineItemReader;
import pt.scml.fin.job.ad.utils.ADFileUtils;
import pt.scml.fin.job.ad.utils.ADParameterValidator;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
import pt.scml.fin.model.dto.enums.GameEnum;


/**
 * Configuration class for the Apostas Desportivas (AD) batch job.
 * <p>
 * This class extends {@link AbstractJobConfig} and defines the Spring Batch job responsible for
 * processing Apostas Desportivas (AD) sales, cancellations, and paid prizes.
 * <p>
 * The job executes the following main steps:
 * <ol>
 *     <li>Validates input parameters and initializes execution context.</li>
 *     <li>Loads and caches required configuration and metadata (e.g., game/channel IDs).</li>
 *     <li>Processes AD input files, reading each line and transforming it into financial invoice data.</li>
 *     <li>Writes processed data to the destination table.</li>
 *     <li>Handles file movement and cleanup via a listener.</li>
 * </ol>
 * <p>
 * This configuration uses components such as:
 * <ul>
 *     <li>{@link ADLineItemReader} - for reading raw input lines.</li>
 *     <li>{@link ADPayedPrizesProcessor} - for transforming raw input into invoice DTOs.</li>
 *     <li>{@link FinDailyInvoiceWriter} - for persisting invoice data.</li>
 *     <li>{@link StepMoveFileListener} - for move file operations.</li>
 * </ul>
 *
 * @see AbstractJobConfig
 * @see ADLineItemReader
 * @see ADPayedPrizesProcessor
 * @see FinDailyInvoiceWriter
 * @see StepMoveFileListener
 */

@Slf4j
@Configuration
@EnableJpaAuditing
@EnableTask
@ComponentScan({"pt.scml.fin.batch.core"})
public class JobConfig extends AbstractJobConfig {

    private static final String JOB_NAME = "AD";
    private static final int CHUNK_SIZE = 1000;
    private final ContextHeader contextHeader;
    private final ContextCache contextCache;
    private final JobParameters jobParameters;
    private final FinUtilsService finUtilsService;
    private final ADFileUtils adFileUtils;
    private final ADParameterValidator adParameterValidator;
    private final StepMoveFileListener stepMoveFileListener;
    private final ADLineItemReader adLineItemReader;

    public JobConfig(
        @Qualifier("finTransactionManager") PlatformTransactionManager transactionManager,
        JobRepository jobRepository,
        ContextHeader contextHeader, ContextCache contextCache,
        JobParameters jobParameters, FinUtilsService finUtilsService, ADFileUtils adFileUtils,
        StepMoveFileListener stepMoveFileListener, ADLineItemReader adLineItemReader) {
        super(JOB_NAME, jobRepository, transactionManager);
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.jobParameters = jobParameters;
        this.finUtilsService = finUtilsService;
        this.adParameterValidator = new ADParameterValidator(contextHeader);
        this.adFileUtils = adFileUtils;
        this.stepMoveFileListener = stepMoveFileListener;
        this.adLineItemReader = adLineItemReader;
    }

    @PostConstruct
    void init() throws IOException {
        this.adParameterValidator.validate(jobParameters);
        loadCacheData();
        contextHeader.setFilename(adFileUtils.hasFilesToProcess());
    }

    private void loadCacheData() {
        log.info("Loading cache data..");
        String jobFolder = finUtilsService.getConfigValue("ADINVOICINGMNG_MODULE_DIR");
        adFileUtils.loadDirectoryCacheData(jobFolder);
        contextCache.setChannelId(finUtilsService.getRegularChannel());
        contextCache.setGameId(finUtilsService.getGameIdByGameShdesAndChannelId(
            GameEnum.APOSTAS_DESPORTIVAS.getShdes(), contextCache.getChannelId()));
        log.info("Loaded cache data.");
    }

    @Override
    protected Step createInitialStep() {
        ItemWriter<FinDailyInvoiceDTO> writer;
        log.info("Creating initial step for AD sales processing");

        ItemProcessor<ADDataDTO, FinDailyInvoiceDTO> processorADSales = new ADPayedPrizesProcessor(
            this.contextHeader, this.contextCache);
        writer = new FinDailyInvoiceWriter(this.finUtilsService);

        return createChunkStep("step1-ad", CHUNK_SIZE, adLineItemReader.reader(),
            processorADSales, writer);

    }

    @Override
    protected Optional<StepExecutionListener> addStepListener() {
        return Optional.of((stepMoveFileListener));
    }
}
