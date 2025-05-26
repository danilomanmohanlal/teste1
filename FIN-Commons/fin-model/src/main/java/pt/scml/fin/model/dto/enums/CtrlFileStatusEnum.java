package pt.scml.fin.model.dto.enums;

import lombok.Getter;

@Getter
public enum CtrlFileStatusEnum {

    PROCESSED("P"),
    ERROR("E"),
    EXECUTION("X"),
    DUPLICATE("D");

    private final String code;

    CtrlFileStatusEnum(String code) {
        this.code = code;
    }
}
