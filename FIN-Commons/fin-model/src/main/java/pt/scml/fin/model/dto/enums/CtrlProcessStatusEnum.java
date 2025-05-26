package pt.scml.fin.model.dto.enums;

import lombok.Getter;

@Getter
public enum CtrlProcessStatusEnum {

    EXECUTING("X"),
    CANCEL("C"),
    SUCCESS("S"),
    ERROR("E"),
    HOLD("O");

    private final String code;

    CtrlProcessStatusEnum(String code) {
        this.code = code;
    }

}
