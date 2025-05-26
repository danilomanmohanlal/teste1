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
import pt.scml.fin.model.dto.FinModuleDTO;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FIN_MODULE")
public class FinModule extends BaseEntity {

    @Id
    @Column(name = "FIN_MODULE_ID", nullable = false)
    private Long finModuleId;

    @Column(name = "SHDES", nullable = false, length = 20)
    private String shortDescription;

    @Column(name = "DES", nullable = false, length = 100)
    private String description;

    @Column(name = "MODULE_TYPE", nullable = false, length = 1)
    private String moduleType;

    @Column(name = "EXECUTABLE_NAME", length = 30)
    private String executableName;

    @Column(name = "VERSION", length = 30)
    private String version;

    @Column(name = "PREV_MODULE_ID", precision = 5)
    private Long prevModuleId;

    // Convert Entity to DTO
    public FinModuleDTO toDTO() {
        return new FinModuleDTO(
            this.finModuleId,
            this.shortDescription,
            this.description,
            this.moduleType,
            this.executableName,
            this.version,
            this.prevModuleId,
            getEntryDate(),
            getLastUpdateDate(),
            getLastUserId()
        );
    }
}
