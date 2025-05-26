package pt.scml.fin.li_ips.processor;

import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.li_ips.model.dto.LIGameDetails;
import pt.scml.fin.li_ips.model.dto.IPSOrderConfirmationDTO;
import pt.scml.fin.li_ips.model.dto.LiData;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

public class IPSOrderConfirmationProcessor implements
        ItemProcessor<IPSOrderConfirmationDTO, Void> {

    private static final Long ZEROL = 0L;

    private final ContextHeader contextHeader;
    private final ContextCache contextCache;
    private final InvoiceAccumulator accumulator;

    public IPSOrderConfirmationProcessor(ContextHeader contextHeader, ContextCache contextCache,
            InvoiceAccumulator accumulator) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.accumulator = accumulator;
    }

    @Override
    public Void process(IPSOrderConfirmationDTO item) throws Exception {
        if (!"DT".equals(item.getRecordType()) || item.getGameDetails() == null) {
            return null;
        }

        for (LIGameDetails game : item.getGameDetails()) {
            if (game.getGameNumber() == null) continue;

//            LiData liData = new LiData(item.getAgentCode(),
//                    null,
//                    item.getFileDate(),
//                    lPadGameType(game.getGameNumber()),
//                    null,
//                    Double.parseDouble(game.getOrderValue()),
//                    Double.parseDouble(game.getOrderRequest()),
//                    Integer.parseInt(game.getOrderRequest())
//                    );

            Double salesAmount = parseDoubleSafe(game.getOrderValue()) / 100.0;
            Long salesQt = Long.parseLong(game.getOrderRequest());



            FinDailyInvoiceDTO dto = FinDailyInvoiceDTO.builder()
                    .finPeriodId(contextHeader.getCurrFinPeriodId())
                    .stationCode(item.getAgentCode())
                    .finProcessId(contextHeader.getControlProcessId())
                    .gameId(contextCache.getGameId())
                    .contestName(lPadGameType(game.getGameNumber()))
                    .salesQt(salesQt)
                    .salesAmount(BigDecimal.valueOf(salesAmount))
                    .prizeQt(ZEROL)
                    .prizeAmount(BigDecimal.ZERO)
                    .cancelQt(ZEROL)
                    .cancelAmount(BigDecimal.ZERO)
                    .channelId(contextCache.getChannelId())
                    .lastUserId(contextHeader.getUserId())
                    .dataRefDate(DateUtils.getLocalDateFromString(contextHeader.getProcDate(), DateUtils.YYYYMMDD))
                    .jobInstanceId(contextHeader.getJobExecutionId())
                    .build();

            accumulator.accumulate(dto);
        }

        return null;
    }

    private Double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String lPadGameType(String str){
        return String.format("%07d", Integer.parseInt(str,10));
    }
}
