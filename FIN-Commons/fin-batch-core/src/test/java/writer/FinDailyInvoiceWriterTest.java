package writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.writer.FinDailyInvoiceWriter;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

@ExtendWith(MockitoExtension.class)
class FinDailyInvoiceWriterTest {

    @Mock
    private FinUtilsService finUtilsService;

    @InjectMocks
    private FinDailyInvoiceWriter writer;

    @Captor
    private ArgumentCaptor<List<FinDailyInvoiceDTO>> invoiceListCaptor;

    @Test
    void testWriteChunkWithItems() {
        // Arrange
        FinDailyInvoiceDTO item1 = FinDailyInvoiceDTO.builder()
            .finPeriodId(202505L)
            .stationCode("ST001")
            .finProcessId(1L)
            .gameId("10")
            .contestName("CONTEST1")
            .drawContestDate(LocalDate.of(2025, 5, 5))
            .prizeQt(10L)
            .prizeAmount(new BigDecimal("100.00"))
            .channelId(1L)
            .lastUserId("user1")
            .dataRefDate(LocalDate.of(2025, 5, 2))
            .jobInstanceId(123L)
            .build();

        FinDailyInvoiceDTO item2 = FinDailyInvoiceDTO.builder()
            .finPeriodId(202505L)
            .stationCode("ST002")
            .finProcessId(1L)
            .gameId("20")
            .contestName("CONTEST2")
            .drawContestDate(LocalDate.of(2025, 5, 6))
            .prizeQt(20L)
            .prizeAmount(new BigDecimal("200.00"))
            .channelId(1L)
            .lastUserId("user2")
            .dataRefDate(LocalDate.of(2025, 5, 2))
            .jobInstanceId(456L)
            .build();

        List<FinDailyInvoiceDTO> itemsToWrite = Arrays.asList(item1, item2);
        Chunk<FinDailyInvoiceDTO> chunk = new Chunk<>(itemsToWrite);
        int expectedNumberOfItems = itemsToWrite.size();

        when(finUtilsService.batchInsertFinDailyInvoice(itemsToWrite)).thenReturn(expectedNumberOfItems);

        // Act
        writer.write(chunk);

        // Assert
        verify(finUtilsService, times(1)).batchInsertFinDailyInvoice(invoiceListCaptor.capture());
        List<FinDailyInvoiceDTO> capturedList = invoiceListCaptor.getValue();
        assertEquals(expectedNumberOfItems, capturedList.size());
        assertEquals(item1, capturedList.get(0));
        assertEquals(item2, capturedList.get(1));
    }

    @Test
    void testWriteChunkWithEmptyChunk() {
        // Arrange
        Chunk<FinDailyInvoiceDTO> chunk = new Chunk<>();
        when(finUtilsService.batchInsertFinDailyInvoice(List.of())).thenReturn(0);

        // Act
        writer.write(chunk);

        // Assert
        verify(finUtilsService, times(1)).batchInsertFinDailyInvoice(invoiceListCaptor.capture());
        List<FinDailyInvoiceDTO> capturedList = invoiceListCaptor.getValue();
        assertEquals(0, capturedList.size());
    }

    @Test
    void testWriteChunkWithSingleItem() {
        // Arrange
        FinDailyInvoiceDTO item = FinDailyInvoiceDTO.builder()
            .finPeriodId(202505L)
            .stationCode("ST003")
            .finProcessId(1L)
            .gameId("30")
            .contestName("CONTEST3")
            .drawContestDate(LocalDate.of(2025, 5, 7))
            .prizeQt(30L)
            .prizeAmount(new BigDecimal("300.00"))
            .channelId(1L)
            .lastUserId("user3")
            .dataRefDate(LocalDate.of(2025, 5, 2))
            .jobInstanceId(789L)
            .build();

        Chunk<FinDailyInvoiceDTO> chunk = new Chunk<>(List.of(item));
        when(finUtilsService.batchInsertFinDailyInvoice(List.of(item))).thenReturn(1);

        // Act
        writer.write(chunk);

        // Assert
        verify(finUtilsService, times(1)).batchInsertFinDailyInvoice(invoiceListCaptor.capture());
        List<FinDailyInvoiceDTO> capturedList = invoiceListCaptor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals(item, capturedList.getFirst());
    }

    @Test
    void testWriteChunkAndVerifyNumberOfPersistedItems() {
        // Arrange
        List<FinDailyInvoiceDTO> itemsToWrite = Arrays.asList(
            FinDailyInvoiceDTO.builder().stationCode("A").build(),
            FinDailyInvoiceDTO.builder().stationCode("B").build(),
            FinDailyInvoiceDTO.builder().stationCode("C").build()
        );
        Chunk<FinDailyInvoiceDTO> chunk = new Chunk<>(itemsToWrite);
        int expectedNumberOfItems = itemsToWrite.size();
        when(finUtilsService.batchInsertFinDailyInvoice(itemsToWrite)).thenReturn(expectedNumberOfItems);

        // Act
        writer.write(chunk);

        // Assert
        verify(finUtilsService, times(1)).batchInsertFinDailyInvoice(itemsToWrite);
    }
}