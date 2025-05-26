package pt.scml.fin.model.dto.enums;

import lombok.Getter;

@Getter
public enum ChannelEnum {

    REGULAR("Regular"),
    PORTAL("Portal Med");

    private final String shdes;

    ChannelEnum(String shdes) {
        this.shdes = shdes;
    }

}
