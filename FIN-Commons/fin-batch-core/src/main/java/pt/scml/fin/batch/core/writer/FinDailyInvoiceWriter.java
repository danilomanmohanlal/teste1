package pt.scml.fin.batch.core.writer;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

@Slf4j
public class FinDailyInvoiceWriter implements ItemWriter<FinDailyInvoiceDTO> {

    private final FinUtilsService finUtilsService;

    public FinDailyInvoiceWriter(FinUtilsService finUtilsService) {
        this.finUtilsService = finUtilsService;
    }

    @Override
    public void write(Chunk<? extends FinDailyInvoiceDTO> chunk) {
        List<FinDailyInvoiceDTO> items = new ArrayList<>(chunk.getItems());

        int numberOfItems = finUtilsService.batchInsertFinDailyInvoice(items);
        log.info("Successfully persisted {} items", numberOfItems);
    }
}
