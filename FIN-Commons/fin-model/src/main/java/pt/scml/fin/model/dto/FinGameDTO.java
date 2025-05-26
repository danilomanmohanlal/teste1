package pt.scml.fin.model.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import pt.scml.fin.model.entities.FinGame;
import pt.scml.fin.model.entities.FinGameId;

public record FinGameDTO(
    String gameId,
    Long channelId,
    String shortDescription,
    String description,
    String gameType,
    LocalDateTime entryDate,
    LocalDateTime lastUpdateDate,
    String lastUserId,
    BigDecimal remunPercentage,
    String gameNumber,
    Long pcSeq,
    String pcName,
    String pcDesc
) {

    public FinGame toEntity() {
        FinGameId id = new FinGameId(gameId, channelId);

        return FinGame.builder()
            .id(id)
            .shortDescription(shortDescription)
            .description(description)
            .gameType(gameType)
            .entryDate(entryDate)
            .lastUpdateDate(lastUpdateDate)
            .lastUserId(lastUserId)
            .remunPercentage(remunPercentage)
            .gameNumber(gameNumber)
            .pcSeq(pcSeq)
            .pcName(pcName)
            .pcDesc(pcDesc)
            .build();
    }
}