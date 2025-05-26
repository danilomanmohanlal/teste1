package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FIN_DAILY_INVOICE")
@Getter
@Setter
public class FinDailyInvoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fin_daily_inv_seq")
    @SequenceGenerator(name = "fin_daily_inv_seq", sequenceName = "FIN_DAILY_INVOICE_ID_SEQ", allocationSize = 1)
    @Column(name = "DAILY_INVOICE_ID")
    private Long dailyInvoiceId;

    @Column(name = "FIN_PERIOD_ID")
    private Long finPeriodId;

    @Column(name = "STATION_CODE", length = 20)
    private String stationCode;

    @Column(name = "FIN_PROCESS_ID")
    private Long finProcessId;

    @Column(name = "GAME_ID", length = 2)
    private String gameId;

    @Column(name = "CONTEST_NAME", length = 7)
    private String contestName;

    @Column(name = "DRAW_CONTEST_DATE")
    private LocalDate drawContestDate;

    @Column(name = "SALES_QT")
    private Long salesQt;

    @Column(name = "SALES_AMOUNT")
    private BigDecimal salesAmount;

    @Column(name = "CANCEL_QT")
    private Long cancelQt;

    @Column(name = "CANCEL_AMOUNT")
    private BigDecimal cancelAmount;

    @Column(name = "PRIZE_QT")
    private Long prizeQt;

    @Column(name = "PRIZE_AMOUNT")
    private BigDecimal prizeAmount;

    @Column(name = "CHANNEL_ID", precision = 1)
    private Long channelId;

    @Column(name = "PREVIOUS_BALANCE")
    private BigDecimal previousBalance;

    @Column(name = "DELIVERED_VALUES")
    private BigDecimal deliveredValues;

    @Column(name = "MOVE_VALUES")
    private BigDecimal moveValues;

    @Column(name = "DIFFERENCE")
    private BigDecimal difference;

    @Column(name = "GAME_TYPE", length = 1)
    private String gameType;

    @Column(name = "AGENT_TYPE", length = 1)
    private String agentType;

    @Column(name = "REMUN_VALUE")
    private BigDecimal remunValue;

    @Column(name = "SUBTOTAL_LN")
    private BigDecimal subtotalLn;

    @Column(name = "EXT_CHANNEL_ID")
    private Long extChannelId;

    @Column(name = "DATA_REF_DATE")
    private LocalDate dataRefDate;

    @Column(name = "JOB_INSTANCE_ID", precision = 38)
    private Long jobInstanceId;

    // Static method to convert Entity to DTO
    public FinDailyInvoiceDTO toDTO() {
        return new FinDailyInvoiceDTO(
            this.dailyInvoiceId,
            this.finPeriodId,
            this.stationCode,
            this.finProcessId,
            this.gameId,
            this.contestName,
            this.drawContestDate,
            this.salesQt,
            this.salesAmount,
            this.cancelQt,
            this.cancelAmount,
            this.prizeQt,
            this.prizeAmount,
            this.channelId,
            this.previousBalance,
            this.deliveredValues,
            this.moveValues,
            this.difference,
            this.gameType,
            this.agentType,
            this.remunValue,
            this.subtotalLn,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId(),
            this.extChannelId,
            this.dataRefDate,
            this.jobInstanceId
        );
    }
}