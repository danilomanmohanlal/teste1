package pt.scml.fin.model.dto;


import java.time.LocalDateTime;
import pt.scml.fin.model.entities.FinConfig;

public record FinConfigDTO(
    Long configId,
    String configValue,
    String description,
    String configName,
    LocalDateTime entryDate,
    LocalDateTime lastUpdateDate,
    String lastUserId
) {

    // Convert DTO to Entity
    public FinConfig toEntity() {
        return FinConfig.builder()
            .configId(this.configId)
            .configValue(this.configValue)
            .description(this.description)
            .configName(this.configName)
            .entryDate(this.entryDate)
            .lastUpdateDate(this.lastUpdateDate)
            .lastUserId(this.lastUserId)
            .build();
    }

}