package pt.scml.fin.li_ips.config;

import static pt.scml.fin.batch.core.utils.FinUtils.createDirectoryIfNotExists;
import static pt.scml.fin.li_ips.JobParameters.IPS_ORDER_CONFIRMATION;
import static pt.scml.fin.li_ips.JobParameters.SCML_ALL_FILES;
import static pt.scml.fin.li_ips.JobParameters.SCML_ALL_VALIDS;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import pt.scml.fin.batch.core.config.AbstractJobConfig;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.listener.MoveFileListener;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.utils.FileUtils;
import pt.scml.fin.li_ips.JobParameters;
import pt.scml.fin.li_ips.StepFactory;
import pt.scml.fin.li_ips.processor.InvoiceAccumulator;
import pt.scml.fin.li_ips.tasklet.FinDailyInvoiceWriter;
import pt.scml.fin.li_ips.utils.LIInvoicingParameterValidator;
import pt.scml.fin.model.dto.enums.GameEnum;
import pt.scml.fin.model.repo.FinDailyInvoiceRepository;

@Slf4j
@Configuration
@EnableTask
@ComponentScan({"pt.scml.fin.batch.core"})
/**
 *
 */
public class JobConfig extends AbstractJobConfig {

    private static final String JOB_NAME = "LI_IPS";
    private static final String MODULE_SHDES = "LIINVOICINGMNG";

    Queue<String> fileToProcessQueue = new LinkedList<>();
    private final JobParameters jobParameters;
    private final LIInvoicingParameterValidator validator;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FinUtilsService finUtilsService;
    private final ContextCache contextCache;
    private final ContextHeader contextHeader;
    private final StepFactory stepFactory;
    private final MoveFileListener moveFileListener;
    private final InvoiceAccumulator invoiceAccumulator;
    private final FinDailyInvoiceRepository finDailyInvoiceRepository;

    final Pattern PATTERN_SALES = Pattern.compile("(?i)^scml_all_sales_.*\\d{8}\\.fil$");
    final Pattern PATTERN_VALIDS = Pattern.compile("(?i)^scml_all_valids_.*\\d{8}\\.fil$");
    final Pattern PATTERN_IPS = Pattern.compile("(?i)^ips_order_confirmation_.*\\d{8}\\.fil$");


    public JobConfig(JobParameters jobParameters,
            LIInvoicingParameterValidator validator,
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FinUtilsService finUtilsService,
            ContextCache contextCache,
            ContextHeader contextHeader,
            MoveFileListener moveFileListener, InvoiceAccumulator invoiceAccumulator,
            FinDailyInvoiceRepository finDailyInvoiceRepository) {
        super(JOB_NAME, jobRepository, transactionManager);
        this.jobParameters = jobParameters;
        this.validator = validator;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.finUtilsService = finUtilsService;
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
        this.invoiceAccumulator = invoiceAccumulator;
        this.finDailyInvoiceRepository = finDailyInvoiceRepository;
        this.stepFactory = new StepFactory(invoiceAccumulator, contextCache, contextHeader,
                this.finUtilsService, this);
        this.moveFileListener = moveFileListener;
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

    @PostConstruct
    public void init() {
        this.validator.validate(this.jobParameters);
        contextHeader.setJobHasAFile(true);
        contextHeader.setModuleShdes(MODULE_SHDES);
        loadCacheData();
    }

    private void loadCacheData() {
        log.info("Loading cache data.. channelId and gameId");
        String jobFolder = finUtilsService.getConfigValue("LOG_LIINVOICINGMNG");
        loadDirectoryCacheData(jobFolder);
        contextCache.setChannelId(finUtilsService.getRegularChannel());
        contextCache.setGameId(
                finUtilsService.getGameIdByGameShdesAndChannelId(GameEnum.M1LHAO.getShdes(),
                        contextCache.getChannelId()));
        log.info("Loaded cache data. channelId {}, gameId {}", contextCache.getChannelId(),
                contextCache.getGameId());

        loadInputFiles();
    }

    private void matchAndStoreFile(File inputDir, Pattern pattern, String key) {
        FilenameFilter filter = (dir, name) -> pattern.matcher(name).matches();
        String[] files = inputDir.list(filter);

        if (files == null || files.length == 0) {
            return;
        }
        if (files.length > 1) {
            throw new IllegalStateException("Multiple files matched for pattern: " + pattern);
        }

        String filename = files[0];
        contextCache.getFileMap().put(key, filename);

        String sExecuteDate = filename.substring(filename.lastIndexOf("_") + 1,
                filename.lastIndexOf("."));
        //contextCache.add(key + "ProcDate", sExecuteDate);

        //contextHeader.setFilename(filename);
    }

    private void loadInputFiles() {

        File fInput = new File(contextCache.getInputDirectory());

        switch (jobParameters.getFileType()) {
            case IPS_ORDER_CONFIRMATION -> {
                matchAndStoreFile(fInput, PATTERN_IPS, "IPS_ORDER_CONFIRMATION");
                fileToProcessQueue.add("IPS_ORDER_CONFIRMATION");
            }
            case SCML_ALL_VALIDS -> {
                matchAndStoreFile(fInput, PATTERN_VALIDS, "ALL_VALIDS");
                fileToProcessQueue.add("ALL_VALIDS");
            }
//            case SCML_ALL_SALES -> {
//
//            }
            case SCML_ALL_FILES -> {
                matchAndStoreFile(fInput, PATTERN_IPS, "IPS_ORDER_CONFIRMATION");
                matchAndStoreFile(fInput, PATTERN_VALIDS, "ALL_VALIDS");
                fileToProcessQueue.add("IPS_ORDER_CONFIRMATION");
                fileToProcessQueue.add("ALL_VALIDS");
            }
        }

    }

//    private void loadInputFiles() {
//
//        File fInput = new File(contextCache.getInputDirectory());
//
//        switch (jobParameters.getFileType()) {
//            case IPS_ORDER_CONFIRMATION -> {
//                FilenameFilter filterIps = (dir, name) -> PATTERN_IPS.matcher(name).matches();
//                String[] list = fInput.list(filterIps);
//                if (list.length > 1) {
//                    log.error(
//                            "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
//                    //throw exception
//                }
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//
//                File fil3 = new File(list[0]);
//                contextHeader.setFilename(fil3.getName());
//                String sExecuteDate = fil3.getAbsolutePath()
//                        .substring(fil3.getAbsolutePath().lastIndexOf("_") + 1,
//                                fil3.getAbsolutePath().lastIndexOf("."));
//                try {
//                    Date dExecuteDate = formatter.parse(sExecuteDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//                contextHeader.setProcDate(sExecuteDate);
//            }
//            case SCML_ALL_VALIDS -> {
//                FilenameFilter filterIps = (dir, name) -> PATTERN_VALIDS.matcher(name).matches();
//                String[] list = fInput.list(filterIps);
//                if (list.length > 1) {
//                    log.error(
//                            "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
//                    //throw exception
//                }
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//
//                File fil3 = new File(list[0]);
//                teste = fil3.getName();
//                contextHeader.setFilename(fil3.getName());
//                String sExecuteDate = fil3.getAbsolutePath()
//                        .substring(fil3.getAbsolutePath().lastIndexOf("_") + 1,
//                                fil3.getAbsolutePath().lastIndexOf("."));
//                try {
//                    Date dExecuteDate = formatter.parse(sExecuteDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//                contextHeader.setProcDate(sExecuteDate);
//            }
//            case SCML_ALL_SALES -> {
//                FilenameFilter filterIps = (dir, name) -> PATTERN_SALES.matcher(name).matches();
//                String[] list = fInput.list(filterIps);
//                if (list.length > 1) {
//                    log.error(
//                            "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
//                    //throw exception
//                }
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//
//                File fil3 = new File(list[0]);
//                contextHeader.setFilename(fil3.getName());
//                String sExecuteDate = fil3.getAbsolutePath()
//                        .substring(fil3.getAbsolutePath().lastIndexOf("_") + 1,
//                                fil3.getAbsolutePath().lastIndexOf("."));
//                try {
//                    Date dExecuteDate = formatter.parse(sExecuteDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//                contextHeader.setProcDate(sExecuteDate);
//            }
//            case SCML_ALL_FILES -> {
//                FilenameFilter filterIps = (dir, name) -> PATTERN_IPS.matcher(name).matches();
//                String[] list = fInput.list(filterIps);
//                if (list.length > 1) {
//                    log.error(
//                            "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
//                    //throw exception
//                }
//
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//
//                File fil3 = new File(list[0]);
//                contextHeader.setFilename(fil3.getName());
//                String sExecuteDate = fil3.getAbsolutePath()
//                        .substring(fil3.getAbsolutePath().lastIndexOf("_") + 1,
//                                fil3.getAbsolutePath().lastIndexOf("."));
//                try {
//                    Date dExecuteDate = formatter.parse(sExecuteDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//                contextHeader.setProcDate(sExecuteDate);
//
//
//                FilenameFilter filterValids = (dir, name) -> PATTERN_VALIDS.matcher(name).matches();
//                String[] list2 = fInput.list(filterValids);
//                if (list.length > 1) {
//                    log.error(
//                            "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
//                    //throw exception
//                }
//
//                File fil2 = new File(list2[0]);
//
//                teste =fil2.getName();
//            }
//        }
//
//    }

    @Override
    protected Step createInitialStep() {
        return getFileToBeMovedStep();
    }

    public Step getFileToBeMovedStep() {
        return createTaskletStep("prepareFileToBeMoved", (contribution, chunkContext) -> {
            contextHeader.setFilename(contextCache.getFileMap().get(fileToProcessQueue.poll()));
            return RepeatStatus.FINISHED;
        });
    }

    @Override
    protected List<Step> addNextSteps() {

        List<Step> steps = new ArrayList<>();

        switch (jobParameters.getFileType()) {
            case IPS_ORDER_CONFIRMATION ->
                    steps.addAll(stepFactory.getStepsForProcess("IPS_ORDER_CONFIRMATION"));
            case SCML_ALL_VALIDS -> steps.addAll(stepFactory.getStepsForProcess("ALL_VALIDS"));
//            case SCML_ALL_SALES ->
//                    steps.add(stepFactory.getStepForProcess("ALL_SALES"));
            case SCML_ALL_FILES -> steps.addAll(stepFactory.getStepsForProcess("ALL_FILES"));
            default -> throw new IllegalStateException(
                    "Unexpected value: " + jobParameters.getFileType());
        }

        steps.add(createTaskletStep("last-step",
                new FinDailyInvoiceWriter(this.finDailyInvoiceRepository,
                        this.invoiceAccumulator, this.finUtilsService)));

        return steps;
    }

    @Override
    protected Optional<StepExecutionListener> addStepListener() {
        return Optional.of(moveFileListener);
    }
}
