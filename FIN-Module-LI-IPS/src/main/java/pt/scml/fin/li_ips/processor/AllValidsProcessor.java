package pt.scml.fin.li_ips.processor;

import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.li_ips.model.dto.LiDetailsValid;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

public class AllValidsProcessor implements ItemProcessor<LiDetailsValid, Void> {

    private final InvoiceAccumulator accumulator;
    private final ContextHeader contextHeader;
    private final ContextCache contextCache;

    public AllValidsProcessor(InvoiceAccumulator accumulator,
                               ContextHeader contextHeader,
                               ContextCache contextCache) {
        this.accumulator = accumulator;
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    @Override
    public Void process(LiDetailsValid item) throws Exception {
        // Only process if GameType is "1"
        if (!"1".equals(item.getGameType())) {
            return null;
        }

        // Aggregate into FinDailyInvoiceDTO
        FinDailyInvoiceDTO dto = FinDailyInvoiceDTO.builder()
                .finPeriodId(contextHeader.getCurrFinPeriodId())
                .stationCode(item.getAgentCode())
                .finProcessId(contextHeader.getControlProcessId())
                .gameId(contextCache.getGameId())
                .contestName(item.getGameNum())
                .salesQt(0L)
                .salesAmount(BigDecimal.valueOf(item.getTotalTransactionValid()))
                .prizeQt(0L)
                .prizeAmount(BigDecimal.ZERO)
                .cancelQt(0L)
                .cancelAmount(BigDecimal.ZERO)
                .channelId(contextCache.getChannelId())
                .lastUserId(contextHeader.getUserId())
                .dataRefDate(DateUtils.getLocalDateFromString(contextHeader.getProcDate(), DateUtils.YYYYMMDD))
                .jobInstanceId(contextHeader.getJobExecutionId())
                .build();

        accumulator.accumulate(dto);

        return null;
    }
}
