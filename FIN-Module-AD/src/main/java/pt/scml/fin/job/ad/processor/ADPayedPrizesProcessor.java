package pt.scml.fin.job.ad.processor;

import java.math.BigDecimal;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.job.ad.dto.ADDataDTO;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;


@Slf4j
public class ADPayedPrizesProcessor implements ItemProcessor<ADDataDTO, FinDailyInvoiceDTO> {

    private final ContextHeader contextHeader;
    private final ContextCache contextCache;

    public ADPayedPrizesProcessor(ContextHeader contextHeader, ContextCache contextCache) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    @Override
    public FinDailyInvoiceDTO process(ADDataDTO item) {

        if (item == null || item.isHeader() || item.isTrailer()) {
            return null;
        }

        return FinDailyInvoiceDTO.builder()
            .finPeriodId(contextHeader.getCurrFinPeriodId())
            .stationCode(item.getAgentCode())
            .finProcessId(contextHeader.getControlProcessId())
            .gameId(contextCache.getGameId())
            .contestName(item.last(item.getSettlementID(), 7))
            .drawContestDate(
                DateUtils.getLocalDateFromString(item.getSettlementDate(), DateUtils.YYYYMMDD))
            .prizeQt(Long.valueOf(item.getTotalPrizesPaid150()))
            .prizeAmount(BigDecimal.valueOf(Long.parseLong(item.getTotalPrizesAmountPaid150())))
            .channelId(contextCache.getChannelId())
            .lastUserId(contextHeader.getUserId())
            .dataRefDate(
                DateUtils.getLocalDateFromString(contextHeader.getProcDate(), DateUtils.YYYYMMDD))
            .jobInstanceId(contextHeader.getJobExecutionId())
            .build();
    }
}
