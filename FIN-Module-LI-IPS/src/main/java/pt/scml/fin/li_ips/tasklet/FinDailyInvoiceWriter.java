package pt.scml.fin.li_ips.tasklet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.li_ips.processor.InvoiceAccumulator;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
import pt.scml.fin.model.entities.FinDailyInvoice;
import pt.scml.fin.model.repo.FinDailyInvoiceRepository;

@Slf4j
public class FinDailyInvoiceWriter implements Tasklet {


    private final FinDailyInvoiceRepository invoiceRepository;
    private final InvoiceAccumulator accumulator; // updated from AggregationStore
    private final FinUtilsService finUtilsService;

    public FinDailyInvoiceWriter(FinDailyInvoiceRepository invoiceRepository,
            InvoiceAccumulator accumulator, FinUtilsService finUtilsService) {
        this.invoiceRepository = invoiceRepository;
        this.accumulator = accumulator;
        this.finUtilsService = finUtilsService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        List<FinDailyInvoiceDTO> aggregatedInvoices = new ArrayList<>(accumulator.getAll());

        if (aggregatedInvoices.isEmpty()) {
            log.info("No invoices to write.");
            return RepeatStatus.FINISHED;
        }

        this.finUtilsService.batchInsertFinDailyInvoice(aggregatedInvoices);

        return RepeatStatus.FINISHED;
    }
}
