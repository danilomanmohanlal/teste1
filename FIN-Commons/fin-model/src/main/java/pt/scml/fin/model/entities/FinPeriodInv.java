package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pt.scml.fin.model.dto.FinPeriodInvDTO;

/**
 * Entity class representing the FIN_PERIOD_INV table in the FIN schema. This entity contains
 * information about financial periods for invoicing.
 */
@Entity
@Table(name = "FIN_PERIOD_INV")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FinPeriodInv extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fin_period_seq")
    @SequenceGenerator(name = "fin_period_seq", sequenceName = "FIN_PERIOD_ID_SEQ", allocationSize = 1)
    @Column(name = "FIN_PERIOD_ID", nullable = false)
    private Long finPeriodId;

    @Column(name = "CYCLE_ID", nullable = false)
    private Long cycleId;

    @Column(name = "FIN_CYCLE_NAME", length = 3)
    private String finCycleName;

    @Column(name = "YEAR", nullable = false, length = 4)
    private String year;

    @Column(name = "FIN_BEGIN_DATE", nullable = false)
    private LocalDateTime finBeginDate;

    @Column(name = "FIN_END_DATE", nullable = false)
    private LocalDateTime finEndDate;

    @Column(name = "FIN_PAY_DATE", nullable = false)
    private LocalDateTime finPayDate;

    @Column(name = "FIN_RETURN_DATE")
    private LocalDateTime finReturnDate;

    @Column(name = "PERIOD_STATUS", nullable = false, length = 1)
    private String periodStatus;

    @Column(name = "FIN_INVOICE_RECEIPT_GEN_DATE")
    private LocalDateTime finInvoiceReceiptGenDate;


    /**
     * Method to create a DTO from an entity.
     *
     * @return A new DTO with values from the entity
     */
    public FinPeriodInvDTO toDTO() {
        return new FinPeriodInvDTO(
            this.finPeriodId,
            this.cycleId,
            this.finCycleName,
            this.year,
            this.finBeginDate,
            this.finEndDate,
            this.finPayDate,
            this.finReturnDate,
            this.periodStatus,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId(),
            this.finInvoiceReceiptGenDate);
    }
}