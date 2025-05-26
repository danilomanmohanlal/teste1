package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pt.scml.fin.model.dto.ControlFileDTO;

@Entity
@Table(name = "FIN_CTRL_FILE")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class ControlFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fin_seq")
    @SequenceGenerator(name = "fin_seq", sequenceName = "FIN_FILE_ID_SEQ", allocationSize = 1)
    @Column(name = "FIN_FILE_CTRL_ID")
    private Long fileControlId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIN_PROCESS_ID")
    private ControlProcess processId;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "PROCESSED_DATE")
    private LocalDateTime processedDate;

    @Column(name = "FILE_NAME")
    private String filename;

    @Column(name = "NUM_REC_TOTAL")
    private Long numRecTotal;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATA_START_DATE")
    private LocalDateTime dataStartDate;

    @Column(name = "DATA_END_DATE")
    private LocalDateTime dataEndDate;

    public ControlFileDTO toDTO() {
        return ControlFileDTO.builder()
            .fileControlId(this.fileControlId)
            .processId(this.processId != null ? this.processId.toDTO() : null)
            .creationDate(this.creationDate)
            .processedDate(this.processedDate)
            .filename(this.filename)
            .numRecTotal(this.numRecTotal)
            .status(this.status)
            .dataStartDate(this.dataStartDate)
            .dataEndDate(this.dataEndDate)
            .build();
    }
}
