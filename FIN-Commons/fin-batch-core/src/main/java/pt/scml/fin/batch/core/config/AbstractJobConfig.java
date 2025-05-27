package pt.scml.fin.batch.core.config;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.listener.ChunkAnalyzerListener;
import pt.scml.fin.batch.core.listener.JobControlListener;

import pt.scml.fin.batch.core.listener.StepExceptionListener;
import pt.scml.fin.batch.core.listener.TaskAnalyzerListener;
import pt.scml.fin.batch.core.service.FinUtilsService;

/**
 * Abstract configuration class for defining a Spring Batch job. Subclasses must provide specific
 * initial and subsequent step configurations.
 */
@Slf4j
@Configuration
public abstract class AbstractJobConfig {

    private final String jobName;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    private StepExceptionListener stepExceptionListener;

    @Autowired
    private ChunkAnalyzerListener chunkAnalyzerListener;

    @Autowired
    private TaskAnalyzerListener taskAnalyzerListener;

    @Autowired
    private JobControlListener jobControlListener;

    @Autowired
    private ContextHeader contextHeader;

    @Autowired
    private ContextCache contextCache;

    @Autowired
    private FinUtilsService finUtilsService;

    private Job job;

    protected AbstractJobConfig(String jobName, JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        this.jobName = jobName;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Defines the initial step for the job.
     *
     * @return the initial {@link Step}
     */
    protected abstract Step createInitialStep();

    /**
     * Provides the list of steps to be executed after the initial step.
     *
     * @return a list of {@link Step}
     */
    protected List<Step> addNextSteps() {
        return Collections.emptyList();
    }

    /**
     * @return
     */
    protected Optional<List<StepExecutionListener>> addStepListener() {
        return Optional.empty();
    }

    /**
     * Builds the job configuration including steps and listeners.
     */
    private void buildJob() {

        JobBuilder jobBuilder = new JobBuilder(jobName, jobRepository);

        // Delegate step configuration to subclasses
        SimpleJobBuilder simpleJobBuilder = jobBuilder.start(createInitialStep());

        configureAdditionalSteps(simpleJobBuilder);
        configureAdditionalListeners(simpleJobBuilder);

        this.job = simpleJobBuilder.incrementer(new RunIdIncrementer()).build();
    }

    private void configureAdditionalSteps(SimpleJobBuilder jobBuilder) {
        addNextSteps().forEach(jobBuilder::next);
    }

    private void configureAdditionalListeners(SimpleJobBuilder jobBuilder) {

        //add job life cycle listener
        jobBuilder.listener(jobControlListener);

        //add task execution listener ( for SCDF execution )
        jobBuilder.listener(taskAnalyzerListener);
    }

    /**
     * Adds partitioning to a step using a provided partitioner.
     *
     * @param step        the base step to partition
     * @param partitioner the partitioner used to split the step
     * @param gridSize    the number of partitions
     * @return a partitioned {@link Step}
     */
    protected Step addPartitionOnStep(Step step, Partitioner partitioner, int gridSize) {
        return new StepBuilder(step.getName() + ".manager", jobRepository)
                .partitioner(step.getName(), partitioner)
                .partitionHandler(getPartitionHandler(step, gridSize))
                .listener(stepExceptionListener)
                .build();
    }

    /**
     * Auxiliary method to help create chunk based steps
     *
     * @param stepName
     * @param chunkSize
     * @param reader
     * @param processor
     * @param writer
     * @return a configured chunk {@link Step}
     */
    public <I, O> Step createChunkStep(String stepName, int chunkSize,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer) {
        StepBuilder stepBuilder = new StepBuilder(stepName, jobRepository);
        SimpleStepBuilder<I, O> chunkBuilder = stepBuilder
                .<I, O>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepExceptionListener)
                .listener(chunkAnalyzerListener);

        Optional<List<StepExecutionListener>> stepExecutionListener = addStepListener();
        List<StepExecutionListener> stepExecutionListeners = stepExecutionListener.get();
        stepExecutionListeners.forEach(chunkBuilder::listener);

        return chunkBuilder.build();
    }

    /**
     * Auxiliary method to help create tasklet based steps
     *
     * @param stepName
     * @param tasklet
     * @return a configured chunk {@link Step}
     */
    protected Step createTaskletStep(String stepName, Tasklet tasklet) {
        return new StepBuilder(stepName, jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Job job() {
        if (this.job == null) {
            buildJob();
        }
        return this.job;
    }

    /**
     * Configures a partition handler for parallel execution of step partitions.
     *
     * @param step     the step to execute in partitions
     * @param gridSize number of partitions
     * @return configured {@link PartitionHandler}
     */
    private PartitionHandler getPartitionHandler(Step step, int gridSize) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setTaskExecutor(taskExecutor(gridSize));
        partitionHandler.setStep(step);
        partitionHandler.setGridSize(gridSize);
        return partitionHandler;
    }

    private TaskExecutor taskExecutor(int gridSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(gridSize);
        executor.setThreadNamePrefix("fin-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(20);
        executor.initialize();
        return executor;
    }

    //TODO: think of a better place to put this code


}
