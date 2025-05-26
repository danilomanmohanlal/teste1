package pt.scml.fin.model.entities;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pt.scml.fin.model.dto.FinGameDTO;


@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FIN_GAME")
@Getter
@Setter
public class FinGame extends BaseEntity {

    @EmbeddedId
    private FinGameId id;

    @Column(name = "SHDES", length = 20)
    private String shortDescription;

    @Column(name = "DES", length = 100)
    private String description;

    @Column(name = "GAME_TYPE", length = 2, nullable = false)
    private String gameType;

    @Column(name = "REMUN_PERCENTAGE", precision = 4, scale = 2, nullable = false)
    private BigDecimal remunPercentage;

    @Column(name = "GAME_NUMBER", length = 2)
    private String gameNumber;

    @Column(name = "PC_SEQ")
    private Long pcSeq;

    @Column(name = "PC_NAME", length = 100)
    private String pcName;

    @Column(name = "PC_DESC", length = 100)
    private String pcDesc;

    public FinGameDTO toDTO() {
        return new FinGameDTO(
            this.id.getGameId(),
            this.id.getChannelId(),
            this.shortDescription,
            this.description,
            this.gameType,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId(),
            this.remunPercentage,
            this.gameNumber,
            this.pcSeq,
            this.pcName,
            this.pcDesc
        );
    }
}

