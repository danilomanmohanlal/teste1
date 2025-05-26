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
import pt.scml.fin.model.dto.FinConfigDTO;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "FIN_CONFIG")
public class FinConfig extends BaseEntity {

    @Id
    @Column(name = "CONFIG_ID", nullable = false)
    private Long configId;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 2000)
    private String configValue;

    @Column(name = "DES", nullable = false, length = 500)
    private String description;

    @Column(name = "CONFIG_NAME", nullable = false, length = 100)
    private String configName;

    // Convert Entity to DTO
    public FinConfigDTO toDTO() {
        return new FinConfigDTO(
            this.configId,
            this.configValue,
            this.description,
            this.configName,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId()
        );
    }
}