package pt.scml.fin.model.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pt.scml.fin.model.entities.ControlFile;

/**
 * Record representing a file control entity.
 */
@Builder
public record ControlFileDTO(
    Long fileControlId,
    ControlProcessDTO processId,
    LocalDateTime creationDate,
    LocalDateTime processedDate,
    String filename,
    Long numRecTotal,
    String status,
    LocalDateTime dataStartDate,
    LocalDateTime dataEndDate,
    String lastUserId
) {

    public ControlFile toEntity() {
        return ControlFile.builder()
            .fileControlId(this.fileControlId)
            .processId(this.processId != null ? this.processId.toEntity() : null)
            .creationDate(this.creationDate)
            .processedDate(this.processedDate)
            .filename(this.filename)
            .numRecTotal(this.numRecTotal)
            .status(this.status)
            .dataStartDate(this.dataStartDate)
            .dataEndDate(this.dataEndDate)
            .lastUserId(this.lastUserId)
            .build();
    }
}