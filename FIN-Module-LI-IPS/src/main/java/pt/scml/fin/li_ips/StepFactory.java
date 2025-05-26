package pt.scml.fin.li_ips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.batch.core.Step;
import pt.scml.fin.batch.core.config.AbstractJobConfig;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.li_ips.config.JobConfig;
import pt.scml.fin.li_ips.processor.AllValidsProcessor;
import pt.scml.fin.li_ips.processor.IPSOrderConfirmationProcessor;
import pt.scml.fin.li_ips.processor.InvoiceAccumulator;
import pt.scml.fin.li_ips.reader.AllValidsReader;
import pt.scml.fin.li_ips.reader.IPSOrderConfirmationReader;
import pt.scml.fin.li_ips.tasklet.ClearValues;
import pt.scml.fin.li_ips.writer.NoOpWriter;


public class StepFactory {

    private final InvoiceAccumulator invoiceAccumulator;
    private final ContextCache contextCache;
    private final ContextHeader contextHeader;
    private final FinUtilsService finUtilsService;
    private final JobConfig jobConfig;
    private final Map<String, Supplier<List<Step>>> stepSuppliers = new HashMap<>();
    private static final Integer CHUNK_SIZE = 1000;

    public StepFactory(InvoiceAccumulator invoiceAccumulator, ContextCache contextCache,
            ContextHeader contextHeader, FinUtilsService finUtilsService,
            JobConfig jobConfig) {
        this.invoiceAccumulator = invoiceAccumulator;
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
        this.finUtilsService = finUtilsService;
        this.jobConfig = jobConfig;

        this.stepSuppliers.put("IPS_ORDER_CONFIRMATION", this::getProcessIPSOrderConfirmationSteps);
        this.stepSuppliers.put("ALL_VALIDS", this::getProcessAllValidsSteps);
//        this.stepSuppliers.put("ALL_SALES", this::getProcessAllSalesSteps);
        this.stepSuppliers.put("ALL_FILES", this::getProcessAllFilesSteps);

    }

    public List<Step> getStepsForProcess(String processType) {
        Supplier<List<Step>> supplier = stepSuppliers.get(processType);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown process type: " + processType);
        }

        return supplier.get();
    }

    private List<Step> getProcessIPSOrderConfirmationSteps() {
        IPSOrderConfirmationReader reader = new IPSOrderConfirmationReader(
                contextCache.getWorkDirectory() + contextCache.getFileMap().get("IPS_ORDER_CONFIRMATION"));

        return
                List.of(
                        this.jobConfig.createChunkStep(
                                "step-IPS-Order-Confirmation",
                                CHUNK_SIZE,
                                reader.reader(),
                                new IPSOrderConfirmationProcessor(this.contextHeader,
                                        this.contextCache, this.invoiceAccumulator),
                                new NoOpWriter()));
    }

    private List<Step> getProcessAllValidsSteps() {
        AllValidsReader reader = new AllValidsReader(
                contextCache.getWorkDirectory() + contextCache.getFileMap().get("ALL_VALIDS"));
                //contextCache.getWorkDirectory() + contextHeader.getFilename());

        return List.of(
                this.jobConfig.createChunkStep(
                        "step-All-valids",
                        CHUNK_SIZE,
                        reader.reader(),
                        new AllValidsProcessor(this.invoiceAccumulator, this.contextHeader,
                                this.contextCache),
                        new NoOpWriter()));
    }

    private List<Step> getProcessAllFilesSteps() {

        List<Step> allSteps = new ArrayList<>(getProcessIPSOrderConfirmationSteps());
        allSteps.add(jobConfig.getFileToBeMovedStep());
        allSteps.addAll(getProcessAllValidsSteps());

        return allSteps;
    }


//    private Step getProcessAllSalesSteps() {
//        AllSalesReader reader = new AllSalesReader(contextCache.getWorkDirectory() + contextHeader.getFilename());
//
//        return
//                this.jobConfig.createChunkStep(
//                        "step-IPS-Order-Confirmation",
//                        CHUNK_SIZE,
//                        reader.reader(),
//                        new AllSalesProcessor(this.invoiceAccumulator, this.contextHeader, this.contextCache),
//                        new NoOpWriter());
//    }


}
