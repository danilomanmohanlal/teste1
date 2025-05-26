//package pt.scml.fin.li_ips.writer;
//
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.item.Chunk;
//import org.springframework.batch.item.ItemWriter;
//import pt.scml.fin.batch.core.service.FinUtilsService;
//import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
//
//@Slf4j
//public class FinDailyInvoiceWriter implements ItemWriter<List<FinDailyInvoiceDTO>> {
//
//    private final FinUtilsService finUtilsService;
//
//    public FinDailyInvoiceWriter(FinUtilsService finUtilsService) {
//        this.finUtilsService = finUtilsService;
//    }
//
//    @Override
//    public void write(Chunk<? extends List<FinDailyInvoiceDTO>> chunk) {
//        List<FinDailyInvoiceDTO> flatList = chunk.getItems()
//                .stream()
//                .flatMap(List::stream)
//                .toList();
//
//        int numberOfItems = finUtilsService.batchInsertFinDailyInvoice(flatList);
//        log.info("Successfully persisted {} items", numberOfItems);
//    }
//}
