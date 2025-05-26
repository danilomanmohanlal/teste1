package pt.scml.fin.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pt.scml.fin.model.entities.FinDailyInvoice;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FinDailyInvoiceDTO {

    private Long dailyInvoiceId;
    private Long finPeriodId;
    private String stationCode;
    private Long finProcessId;
    private String gameId;
    private String contestName;
    private LocalDate drawContestDate;
    private Long salesQt;
    private BigDecimal salesAmount;
    private Long cancelQt;
    private BigDecimal cancelAmount;
    private Long prizeQt;
    private BigDecimal prizeAmount;
    private Long channelId;
    private BigDecimal previousBalance;
    private BigDecimal deliveredValues;
    private BigDecimal moveValues;
    private BigDecimal difference;
    private String gameType;
    private String agentType;
    private BigDecimal remunValue;
    private BigDecimal subtotalLn;
    private LocalDateTime entryDate;
    private LocalDateTime lastUpdateDate;
    private String lastUserId;
    private Long extChannelId;
    private LocalDate dataRefDate;
    private Long jobInstanceId;

    public FinDailyInvoice toEntity() {
        return FinDailyInvoice.builder()
            .dailyInvoiceId(dailyInvoiceId)
            .finPeriodId(finPeriodId)
            .stationCode(stationCode)
            .finProcessId(finProcessId)
            .gameId(gameId)
            .contestName(contestName)
            .drawContestDate(drawContestDate)
            .salesQt(salesQt)
            .salesAmount(salesAmount)
            .cancelQt(cancelQt)
            .cancelAmount(cancelAmount)
            .prizeQt(prizeQt)
            .prizeAmount(prizeAmount)
            .channelId(channelId)
            .previousBalance(previousBalance)
            .deliveredValues(deliveredValues)
            .moveValues(moveValues)
            .difference(difference)
            .gameType(gameType)
            .agentType(agentType)
            .remunValue(remunValue)
            .subtotalLn(subtotalLn)
            .entryDate(entryDate)
            .lastUpdateDate(lastUpdateDate)
            .lastUserId(lastUserId)
            .extChannelId(extChannelId)
            .dataRefDate(dataRefDate)
            .jobInstanceId(jobInstanceId)
            .build();
    }
}