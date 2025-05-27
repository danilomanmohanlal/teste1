package pt.scml.fin.job.li_itms.processor;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

@Slf4j
public class LiITMSFileProcessor implements ItemProcessor<LiITMSFileDTO, FinDailyInvoiceDTO> {

    private final ContextHeader contextHeader;
    private final ContextCache contextCache;

    public LiITMSFileProcessor(ContextHeader contextHeader, ContextCache contextCache) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    @Override
    public FinDailyInvoiceDTO process(LiITMSFileDTO item) throws Exception {
        return FinDailyInvoiceDTO.builder()
            .finPeriodId(contextHeader.getCurrFinPeriodId())
            .stationCode(item.getTerminalNumber())
            .finProcessId(contextHeader.getControlProcessId())
            .gameId(contextCache.getGameId())
            .contestName(item.getGameNumber())
            .salesQt(item.getQtyOfBooks())
            .salesAmount(item.getAmountOfBooks())
            .cancelQt(0L)
            .cancelAmount(BigDecimal.ZERO)
            .prizeQt(item.getQtyPaidFirstTier())
            .prizeAmount(item.getAmountPaidFirstTier())
            .channelId(contextCache.getChannelId())
            .previousBalance(BigDecimal.ZERO)
            .deliveredValues(BigDecimal.ZERO)
            .moveValues(BigDecimal.ZERO)
            .difference(BigDecimal.ZERO)
            .remunValue(BigDecimal.ZERO)
            .subtotalLn(BigDecimal.ZERO)
            .lastUserId(contextHeader.getUserId())
            .dataRefDate(
                DateUtils.getLocalDateFromString(contextHeader.getProcDate(), DateUtils.YYYYMMDD))
            .jobInstanceId(contextHeader.getJobExecutionId())
            .build();
    }
}
