package pt.scml.fin.model.dto.enums;

import lombok.Getter;

@Getter
public enum GameEnum {

    LOTARIA_INSTANTANEA("LI"),
    EUROMILHOES("EM"),
    APOSTAS_DESPORTIVAS("AD"),
    M1LHAO("SM"),
    APOSTAS_MUTUAS("AM");

    private final String shdes;

    GameEnum(String shdes) {
        this.shdes = shdes;
    }
}