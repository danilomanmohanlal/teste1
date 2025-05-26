package pt.scml.fin.model.dto;

import java.time.LocalDateTime;
import pt.scml.fin.model.entities.FinChannel;

public record FinChannelDTO(
    Long channelId,
    String shortDescription,
    Long abpChannelId,
    LocalDateTime entryDate,
    LocalDateTime lastUpdateDate,
    String lastUserId
) {

    // Method to convert DTO to Entity
    public FinChannel toEntity() {
        return FinChannel.builder()
            .channelId(channelId)
            .shortDescription(shortDescription)
            .abpChannelId(abpChannelId)
            .entryDate(entryDate)
            .lastUpdateDate(lastUpdateDate)
            .lastUserId(lastUserId)
            .build();
    }


}