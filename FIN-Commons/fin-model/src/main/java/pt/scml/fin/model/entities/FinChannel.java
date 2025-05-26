package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import pt.scml.fin.model.dto.FinChannelDTO;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FIN_CHANNEL")
public class FinChannel extends BaseEntity {

    @Id
    @Column(name = "CHANNEL_ID", precision = 1, scale = 0, nullable = false)
    private Long channelId;

    @Column(name = "SHDES", length = 20)
    private String shortDescription;

    @Column(name = "ABP_CHANNEL_ID", precision = 2, scale = 0, nullable = false)
    private Long abpChannelId;

    // Static method to convert Entity to DTO
    public FinChannelDTO toDTO() {
        return new FinChannelDTO(
            this.channelId,
            this.shortDescription,
            this.abpChannelId,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId()
        );
    }
}
