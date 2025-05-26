//package pt.scml.fin.li_ips.processor;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.annotation.AfterStep;
//import org.springframework.batch.item.ItemProcessor;
//import pt.scml.fin.batch.core.context.ContextCache;
//import pt.scml.fin.batch.core.context.ContextHeader;
//import pt.scml.fin.batch.core.utils.DateUtils;
//import pt.scml.fin.li_ips.model.dto.LiData;
//import pt.scml.fin.li_ips.model.dto.LiDetails;
//import pt.scml.fin.li_ips.model.dto.LiDetailsInvoice;
//import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
//
//public class AllSalesProcessor implements ItemProcessor<LiDetails, Void> {
//
//    private final InvoiceAccumulator accumulator;
//    private final ContextHeader contextHeader;
//    private final ContextCache contextCache;
//
//    // Two temporary maps, like legacy logic
//    private final Map<String, LiData> adqMap = new HashMap<>();
//    private final Map<String, LiData> devMap = new HashMap<>();
//
//    public AllSalesProcessor(InvoiceAccumulator accumulator,
//            ContextHeader contextHeader,
//            ContextCache contextCache) {
//        this.accumulator = accumulator;
//        this.contextHeader = contextHeader;
//        this.contextCache = contextCache;
//    }
//
//    @Override
//    public Void process(LiDetails item) throws Exception {
//        if (!"01".equals(item.getGameType())) {
//            throw new Exception("Invalid game type: " + item.getGameType());
//        }
//
//        String key = item.getAgentCode() + item.getGameNum();
//
//        LiData data = new LiData(
//                item.getAgentCode(),
//                item.getTransactionType(),
//                item.getTransactionDate(),
//                item.getGameNum(),
//                item.getGameType(),
//                item.getTransactionVal(),
//                item.getPackageNumber(),
//                1
//        );
//
//        if (data.getTransactionType().contains("01")) {
//            adqMap.merge(key, data, (oldVal, newVal) -> {
//                oldVal.setTransactionVal(oldVal.getTransactionVal() + newVal.getTransactionVal());
//                oldVal.setTotalPackage(oldVal.getTotalPackage() + 1);
//                return oldVal;
//            });
//        } else if (data.getTransactionType().contains("02")) {
//            devMap.merge(key, data, (oldVal, newVal) -> {
//                oldVal.setTransactionVal(oldVal.getTransactionVal() + newVal.getTransactionVal());
//                oldVal.setTotalPackage(oldVal.getTotalPackage() + 1);
//                return oldVal;
//            });
//        }
//
//        return null;
//    }
//
//    @AfterStep
//    public void afterStep(StepExecution stepExecution) {
//        Map<String, LiDetailsInvoice> finalMap = new HashMap<>();
//
//        // First pass: acquired
//        for (Map.Entry<String, LiData> entry : adqMap.entrySet()) {
//            LiData d = entry.getValue();
//            LiDetailsInvoice invoice = null;
//            try {
//                invoice = new LiDetailsInvoice(
//                        d.getAgentCode(),
//                        contextCache.getGameId(),
//                        d.getGameNum(),
//                        d.getTotalPackage(),
//                        d.getTransactionVal(),
//                        0,
//                        0.0,
//                        0,
//                        0.0
//                );
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            finalMap.put(entry.getKey(), invoice);
//        }
//
//        // Second pass: returned
//        for (Map.Entry<String, LiData> entry : devMap.entrySet()) {
//            LiData d = entry.getValue();
//            finalMap.compute(entry.getKey(), (key, existing) -> {
//                if (existing == null) {
//                    try {
//                        return new LiDetailsInvoice(d.getAgentCode(),
//                                contextCache.getGameId(),
//                                d.getGameNum(),
//                                0, 0.0,
//                                d.getTotalPackage(),
//                                d.getTransactionVal(),
//                                0, 0.0);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    existing.setCancelQT(d.getTotalPackage());
//                    existing.setCancelAmount(d.getTransactionVal());
//                    return existing;
//                }
//            });
//        }
//
//        // Now accumulate all into FinDailyInvoiceDTO
//        for (LiDetailsInvoice li : finalMap.values()) {
//            FinDailyInvoiceDTO dto = FinDailyInvoiceDTO.builder()
//                    .finPeriodId(contextHeader.getCurrFinPeriodId())
//                    .stationCode(li.getStationCode())
//                    .finProcessId(contextHeader.getControlProcessId())
//                    .gameId(li.getGameId())
//                    .contestName(li.getContestName())
//                    .salesQt((long) li.getSalesQT())
//                    .salesAmount(BigDecimal.valueOf(li.getSalesAmount()))
//                    .cancelQt((long) li.getCancelQT())
//                    .cancelAmount(BigDecimal.valueOf(li.getCancelAmount()))
//                    .prizeQt(0L)
//                    .prizeAmount(BigDecimal.ZERO)
//                    .channelId(contextCache.getChannelId())
//                    .lastUserId(contextHeader.getUserId())
//                    .dataRefDate(
//                            DateUtils.getLocalDateFromString(contextHeader.getProcDate(),
//                                    DateUtils.YYYYMMDD))
//                    .jobInstanceId(contextHeader.getJobExecutionId())
//                    .build();
//
//            accumulator.accumulate(dto);
//        }
//    }
//}
