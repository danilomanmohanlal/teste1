package pt.scml.fin.job.li_itms.processor;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

class LiITMSFileProcessorTest {

    private ContextHeader contextHeader;
    private ContextCache contextCache;
    private LiITMSFileProcessor processor;

    @BeforeEach
    void setup() {
        contextHeader = Mockito.mock(ContextHeader.class);
        contextCache = Mockito.mock(ContextCache.class);

        Mockito.when(contextHeader.getCurrFinPeriodId()).thenReturn(123L);
        Mockito.when(contextHeader.getControlProcessId()).thenReturn(456L);
        Mockito.when(contextHeader.getProcDate()).thenReturn("20240520");
        Mockito.when(contextHeader.getUserId()).thenReturn("test-user");
        Mockito.when(contextHeader.getJobExecutionId()).thenReturn(789L);

        Mockito.when(contextCache.getGameId()).thenReturn("111L");
        Mockito.when(contextCache.getChannelId()).thenReturn(222L);

        processor = new LiITMSFileProcessor(contextHeader, contextCache);
    }

    @Test
    void testProcess_shouldReturnCorrectFinDailyInvoiceDTO() throws Exception {
        LiITMSFileDTO dto = new LiITMSFileDTO();
        dto.setTerminalNumber("1001L");
        dto.setGameNumber("Game01");
        dto.setQtyOfBooks(10L);
        dto.setAmountOfBooks(new BigDecimal("100.50"));
        dto.setQtyPaidFirstTier(5L);
        dto.setAmountPaidFirstTier(new BigDecimal("50.25"));

        FinDailyInvoiceDTO result = processor.process(dto);

        assertThat(result).isNotNull();
        assertThat(result.getFinPeriodId()).isEqualTo(123L);
        assertThat(result.getStationCode()).isEqualTo("1001L");
        assertThat(result.getFinProcessId()).isEqualTo(456L);
        assertThat(result.getGameId()).isEqualTo("111L");
        assertThat(result.getContestName()).isEqualTo("Game01");
        assertThat(result.getSalesQt()).isEqualTo(10L);
        assertThat(result.getSalesAmount()).isEqualByComparingTo("100.50");
        assertThat(result.getCancelQt()).isEqualTo(0L);
        assertThat(result.getCancelAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getPrizeQt()).isEqualTo(5L);
        assertThat(result.getPrizeAmount()).isEqualByComparingTo("50.25");
        assertThat(result.getChannelId()).isEqualTo(222L);
        assertThat(result.getPreviousBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getLastUserId()).isEqualTo("test-user");
        assertThat(result.getJobInstanceId()).isEqualTo(789L);
        assertThat(result.getDataRefDate()).isNotNull();
    }
}
