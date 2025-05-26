//package pt.scml.fin.li_ips.config;
//
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.FlowBuilder;
//import org.springframework.batch.core.job.flow.Flow;
//import org.springframework.batch.core.job.flow.support.SimpleFlow;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.transaction.PlatformTransactionManager;
//import pt.scml.fin.batch.core.config.AbstractJobConfig;
//import pt.scml.fin.li_ips.CustomReader;
//import pt.scml.fin.li_ips.CustomWriter;
//import pt.scml.fin.li_ips.DummyTasklet;
//import pt.scml.fin.li_ips.StageDecider;
//
////
//// @Slf4j
//// @Configuration
//// @EnableTask
//// @ComponentScan({"pt.scml.fin.batch.core"})
//public class JobConfigFlow extends AbstractJobConfig {
//
//    private static final String JOB_NAME = "LI_Iew";
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final StageDecider stageDecider;
//
//    public JobConfigFlow(JobRepository jobRepository,
//            PlatformTransactionManager transactionManager) {
//        super(JOB_NAME, true, jobRepository, transactionManager);
//        this.jobRepository = jobRepository;
//        this.transactionManager = transactionManager;
//        this.stageDecider = new StageDecider("SALES");
//    }
//
//    public Step stepA() {
//        return createTaskletStep("stepA", new DummyTasklet("STEP A"));
//    }
//
//    public Step stepB() {
//        return createChunkStep("stepB", 100, new CustomReader("stepB"), null, new CustomWriter());
//    }
//
//    public Step stepC() {
//        return createTaskletStep("stepC", new DummyTasklet("STEP C"));
//    }
//
//    public Step stepD() {
//        return createTaskletStep("stepD", new DummyTasklet("STEP D"));
//    }
//
//    @Override
//    protected Flow createConditionalFlow() {
//        return new FlowBuilder<SimpleFlow>("conditionalFlow")
//                .start(stepA()) // read configs and validate - tasklet
//                .next(stageDecider)
//                .on("IPS")
//                .to(stepB()) // IPS  - chunk
//                .from(stageDecider)
//                .on("ALL_VALIDS")
//                .to(stepC()) // ALL VALIDS - chunk
//                .from(stageDecider)
//                .on("SALES")
//                .to(stepD())
//                .build();
//    }
//}
