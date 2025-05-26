//package pt.scml.fin.model.dto;
//
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//import pt.scml.fin.model.entities.FinDailyInvoice;
//
//@Builder
//public record FinDailyInvoiceDTO(
//    Long dailyInvoiceId,
//    Long finPeriodId,
//    String stationCode,
//    Long finProcessId,
//    String gameId,
//    String contestName,
//    LocalDate drawContestDate,
//    Long salesQt,
//    BigDecimal salesAmount,
//    Long cancelQt,
//    BigDecimal cancelAmount,
//    Long prizeQt,
//    BigDecimal prizeAmount,
//    Long channelId,
//    BigDecimal previousBalance,
//    BigDecimal deliveredValues,
//    BigDecimal moveValues,
//    BigDecimal difference,
//    String gameType,
//    String agentType,
//    BigDecimal remunValue,
//    BigDecimal subtotalLn,
//    LocalDateTime entryDate,
//    LocalDateTime lastUpdateDate,
//    String lastUserId,
//    Long extChannelId,
//    LocalDate dataRefDate,
//    Long jobInstanceId
//) {
//
//    // Method to convert DTO to Entity
//    public FinDailyInvoice toEntity() {
//        return FinDailyInvoice.builder()
//            .dailyInvoiceId(dailyInvoiceId)
//            .finPeriodId(finPeriodId)
//            .stationCode(stationCode)
//            .finProcessId(finProcessId)
//            .gameId(gameId)
//            .contestName(contestName)
//            .drawContestDate(drawContestDate)
//            .salesQt(salesQt)
//            .salesAmount(salesAmount)
//            .cancelQt(cancelQt)
//            .cancelAmount(cancelAmount)
//            .prizeQt(prizeQt)
//            .prizeAmount(prizeAmount)
//            .channelId(channelId)
//            .previousBalance(previousBalance)
//            .deliveredValues(deliveredValues)
//            .moveValues(moveValues)
//            .difference(difference)
//            .gameType(gameType)
//            .agentType(agentType)
//            .remunValue(remunValue)
//            .subtotalLn(subtotalLn)
//            .entryDate(entryDate)
//            .lastUpdateDate(lastUpdateDate)
//            .lastUserId(lastUserId)
//            .extChannelId(extChannelId)
//            .dataRefDate(dataRefDate)
//            .jobInstanceId(jobInstanceId)
//            .build();
//    }
//
//}
