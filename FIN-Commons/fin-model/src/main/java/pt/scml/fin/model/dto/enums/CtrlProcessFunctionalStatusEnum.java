package pt.scml.fin.model.dto.enums;

import lombok.Getter;

@Getter
public enum CtrlProcessFunctionalStatusEnum {

    SUCCESS("S"),
    ERROR("E");

    private final String code;

    CtrlProcessFunctionalStatusEnum(String code) {
        this.code = code;
    }

}
