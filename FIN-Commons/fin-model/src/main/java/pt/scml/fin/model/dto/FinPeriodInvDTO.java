package pt.scml.fin.model.dto;

import java.time.LocalDateTime;
import pt.scml.fin.model.entities.FinPeriodInv;

/**
 * Data Transfer Object for FinPeriodInv entity. Implemented as a record for immutability and
 * automatic generation of constructors, getters, equals, hashCode, and toString methods.
 */
public record FinPeriodInvDTO(
    Long finPeriodId,
    Long cycleId,
    String finCycleName,
    String year,
    LocalDateTime finBeginDate,
    LocalDateTime finEndDate,
    LocalDateTime finPayDate,
    LocalDateTime finReturnDate,
    String periodStatus,
    LocalDateTime entryDate,
    LocalDateTime lastUpdateDate,
    String lastUserId,
    LocalDateTime finInvoiceReceiptGenDate) {


    /**
     * Converts this DTO to an entity.
     *
     * @return A new FinPeriodInv entity with values from this DTO
     */
    public FinPeriodInv toEntity() {
        return FinPeriodInv.builder()
            .finPeriodId(finPeriodId)
            .cycleId(cycleId)
            .finCycleName(finCycleName)
            .year(year)
            .finBeginDate(finBeginDate)
            .finEndDate(finEndDate)
            .finPayDate(finPayDate)
            .finReturnDate(finReturnDate)
            .periodStatus(periodStatus)
            .entryDate(entryDate)
            .lastUpdateDate(lastUpdateDate)
            .lastUserId(lastUserId)
            .finInvoiceReceiptGenDate(finInvoiceReceiptGenDate)
            .build();
    }
}
