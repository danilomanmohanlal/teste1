package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pt.scml.fin.model.dto.ControlProcessDTO;


@Entity
@Table(name = "FIN_CTRL_PROCESS")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ControlProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fin_process_seq")
    @SequenceGenerator(name = "fin_process_seq", sequenceName = "FIN_PROCESS_ID_SEQ", allocationSize = 1)
    @Column(name = "FIN_PROCESS_ID", precision = 38)
    private Long controlProcessId;

    @Column(name = "FIN_MODULE_ID", precision = 5)
    private Long moduleId;

    @Column(name = "FIN_PERIOD_ID", precision = 38)
    private Long finPeriodId;

    @Column(name = "PID", length = 50)
    private String pid;

    @Column(name = "USER_ID", length = 16)
    private String userId;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "PROCESS_STATUS", length = 1)
    private String processStatus;

    @Column(name = "FUNCTIONAL_STATUS", length = 1)
    private String functionalStatus;

    @Column(name = "STOP_PROCESS", length = 2)
    private String stopProcess;

    @Column(name = "CALLED_PARAMS", length = 100)
    private String calledParams;

    @Column(name = "FUNCTIONAL_PARAMS", length = 100)
    private String functionalParams;

    @Column(name = "DATA1", length = 200)
    private String data1;

    @Column(name = "DATA2", length = 200)
    private String data2;

    @Column(name = "ERROR_CODE", length = 100)
    private String errorCode;

    @Column(name = "ERROR_MESSAGE", length = 2000)
    private String errorMessage;

    @Column(name = "JOB_INSTANCE_ID", precision = 38)
    private Long jobInstanceId;


    /**
     * Converts a ControlProcess entity to a DTO.
     *
     * @return The corresponding ControlProcessDTO
     */
    public ControlProcessDTO toDTO() {
        return ControlProcessDTO.builder()
            .controlProcessId(this.controlProcessId)
            .moduleId(this.moduleId)
            .finPeriodId(this.finPeriodId)
            .pid(this.pid)
            .userId(this.userId)
            .startDate(this.startDate)
            .endDate(this.endDate)
            .processStatus(this.processStatus)
            .functionalStatus(this.functionalStatus)
            .stopProcess(this.stopProcess)
            .calledParams(this.calledParams)
            .functionalParams(this.functionalParams)
            .data1(this.data1)
            .data2(this.data2)
            .errorCode(this.errorCode)
            .errorMessage(this.errorMessage)
            .jobInstanceId(this.jobInstanceId)
            .build();
    }
}
