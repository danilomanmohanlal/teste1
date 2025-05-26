package pt.scml.fin.model.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import pt.scml.fin.model.entities.ControlProcess;

/**
 * Record representing a control process entity.
 */
@Builder
public record ControlProcessDTO(
    Long controlProcessId,
    Long moduleId,
    Long finPeriodId,
    String pid,
    String userId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String processStatus,
    String functionalStatus,
    String stopProcess,
    String calledParams,
    String functionalParams,
    String data1,
    String data2,
    String errorCode,
    String errorMessage,
    Long jobInstanceId) {

    /**
     * Converts this DTO to a ControlProcess entity.
     *
     * @return The corresponding ControlProcess entity
     */
    public ControlProcess toEntity() {
        return ControlProcess.builder()
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
            .jobInstanceId(jobInstanceId)
            .build();
    }

}