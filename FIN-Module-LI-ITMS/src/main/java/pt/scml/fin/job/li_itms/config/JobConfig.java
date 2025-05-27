package pt.scml.fin.job.li_itms.config;

import static pt.scml.fin.batch.core.utils.FinUtils.createDirectoryIfNotExists;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
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
import pt.scml.fin.batch.core.listener.MoveFileListener;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.utils.FileUtils;
import pt.scml.fin.batch.core.writer.FinDailyInvoiceWriter;
import pt.scml.fin.job.li_itms.JobParameters;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;
import pt.scml.fin.job.li_itms.processor.LiITMSFileProcessor;
import pt.scml.fin.job.li_itms.reader.LiITMSFileReader;
import pt.scml.fin.job.li_itms.utils.LiITMSFileUtils;
import pt.scml.fin.job.li_itms.utils.ParameterValidator;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
import pt.scml.fin.model.dto.enums.GameEnum;


/**
 * Configuration class for the LI (ITMS) batch job.
 * <p>
 * This class extends AbstractJobConfig and is responsible for processing LI-ITMS sales and
 * cancellations, and paid prizes data. It configures and executes a Spring Batch job with multiple
 * steps for different processing tasks.
 * <p>
 * The job performs the following main tasks: 1. Validates input parameters and prepares the
 * execution context 2. Processes ITMS sales and cancellations data 3. Processes ITMS paid prizes
 * data 4. Manages control records for the batch process
 */
@Slf4j
@Configuration
@EnableJpaAuditing
@EnableTask
@ComponentScan({"pt.scml.fin.batch.core"})
public class JobConfig extends AbstractJobConfig {

    protected static final int CHUNK_SIZE = 10;
    private static final String JOB_NAME = "LI-ITMS";
    private final ContextHeader contextHeader;
    private final ContextCache contextCache;
    private final JobParameters jobParameters;
    private final FinUtilsService finUtilsService;
    private final ParameterValidator parameterValidator;
    private final LiITMSFileUtils liFileUtils;
    private final MoveFileListener moveFileListener;


    public JobConfig(
            @Qualifier("finTransactionManager") PlatformTransactionManager transactionManager,
            JobRepository jobRepository,
            ContextHeader contextHeader,
            ContextCache contextCache,
            JobParameters jobParameters,
            FinUtilsService finUtilsService,
            LiITMSFileUtils liFileUtils,
            MoveFileListener moveFileListener) {
        super(JOB_NAME, jobRepository, transactionManager);
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.jobParameters = jobParameters;
        this.finUtilsService = finUtilsService;
        this.parameterValidator = new ParameterValidator(contextHeader);
        this.liFileUtils = liFileUtils;
        this.moveFileListener = moveFileListener;
    }

    @PostConstruct
    void init() throws IOException {
        this.parameterValidator.validate(jobParameters);
        loadCacheData();
        contextHeader.setFilename(liFileUtils.hasFileToProcess());
    }

    private void loadCacheData() {
        log.info("Loading cache data..");
        String jobFolder = finUtilsService.getConfigValue("LOG_LIITMSINVOICING");
        loadDirectoryCacheData(jobFolder);
        contextCache.setChannelId(finUtilsService.getRegularChannel());
        contextCache.setGameId(finUtilsService.getGameIdByGameShdesAndChannelId(
                GameEnum.LOTARIA_INSTANTANEA.getShdes(), contextCache.getChannelId()));
        log.info("Loaded cache data.");
    }

    @Override
    protected Step createInitialStep() {
        ItemWriter<FinDailyInvoiceDTO> writer;
        log.info("Creating initial step for LI-ITMS processing");
        LiITMSFileReader readerITMSFile = new LiITMSFileReader(contextCache, contextHeader);
        ItemProcessor<LiITMSFileDTO, FinDailyInvoiceDTO> processorITMSFile = new LiITMSFileProcessor(
                this.contextHeader, this.contextCache);
        writer = new FinDailyInvoiceWriter(this.finUtilsService);

        if (validateSingleFile()) {
            try {
                return createChunkStep("step1-li-itms", CHUNK_SIZE, readerITMSFile.reader(),
                        processorITMSFile, writer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return createNoFilesToProcessStep();
        }
    }

    @Override
    protected Optional<List<StepExecutionListener>> addStepListener() {
        return Optional.of(List.of(moveFileListener));
    }

    private static final String CONFIG_HOME = "HOME";
    private static final String CONFIG_INPUT = "INPUT";
    private static final String CONFIG_ERROR = "ERROR";
    private static final String CONFIG_SUCCESS = "SUCCESS";
    private static final String CONFIG_WORK = "WORK";
    private static final String CONFIG_DUPLICATED = "DUPLICATED";

    public void loadDirectoryCacheData(String jobFolder) {

        String homePath = finUtilsService.getConfigValue(CONFIG_HOME);
        String inputFolder = finUtilsService.getConfigValue(CONFIG_INPUT);
        String errorFolder = finUtilsService.getConfigValue(CONFIG_ERROR);
        String successFolder = finUtilsService.getConfigValue(CONFIG_SUCCESS);
        String workFolder = finUtilsService.getConfigValue(CONFIG_WORK);
        String duplicatedFolder = finUtilsService.getConfigValue(CONFIG_DUPLICATED);

        String inputDirectory = homePath + jobFolder + inputFolder;
        createDirectoryIfNotExists(inputDirectory);
        contextCache.setInputDirectory(inputDirectory);

        String workDirectory = homePath + jobFolder + workFolder;
        createDirectoryIfNotExists(workDirectory);
        contextCache.setWorkDirectory(workDirectory);

        String successDirectory = homePath + jobFolder + successFolder;
        createDirectoryIfNotExists(successDirectory);
        contextCache.setSuccessDirectory(successDirectory);

        String errorDirectory = homePath + jobFolder + errorFolder;
        createDirectoryIfNotExists(errorDirectory);
        contextCache.setErrorDirectory(errorDirectory);

        String duplicatedDirectory = homePath + jobFolder + duplicatedFolder;
        createDirectoryIfNotExists(duplicatedDirectory);
        contextCache.setDuplicatedDirectory(duplicatedDirectory);

    }

}
