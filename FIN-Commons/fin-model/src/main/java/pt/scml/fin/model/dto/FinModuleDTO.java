package pt.scml.fin.model.dto;


import java.time.LocalDateTime;
import pt.scml.fin.model.entities.FinModule;

public record FinModuleDTO(
    Long finModuleId,
    String shortDescription,
    String description,
    String moduleType,
    String executableName,
    String version,
    Long prevModuleId,
    LocalDateTime entryDate,
    LocalDateTime lastUpdateDate,
    String lastUserId
) {

    // Convert DTO to Entity
    public FinModule toEntity() {
        return FinModule.builder()
            .finModuleId(this.finModuleId)
            .shortDescription(this.shortDescription)
            .description(this.description)
            .moduleType(this.moduleType)
            .executableName(this.executableName)
            .version(this.version)
            .prevModuleId(this.prevModuleId)
            .entryDate(this.entryDate)
            .lastUpdateDate(this.lastUpdateDate)
            .lastUserId(this.lastUserId)
            .build();
    }

}