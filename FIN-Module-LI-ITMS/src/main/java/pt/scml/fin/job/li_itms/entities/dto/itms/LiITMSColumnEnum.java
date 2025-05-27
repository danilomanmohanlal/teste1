package pt.scml.fin.job.li_itms.entities.dto.itms;

import java.util.Arrays;
import lombok.Getter;


@Getter
public enum LiITMSColumnEnum {

    START_DATE("startDate"),
    END_DATE("endDate"),
    GAME_NUMBER("gameNumber"),
    TERMINAL_NUMBER("terminalNumber"),
    EMISSION_NUMBER("EmissionNumber"),
    QTY_OF_BOOKS("QtyOfBooks"),
    AMOUNT_OF_BOOKS("AmountOfBooks"),
    QTY_PAID_FIRST_TIER("QtyPaidFirstTier"),
    AMOUNT_PAID_FIRST_TIER("AmountPaidFirstTier"),
    QTY_PAID_OTHER_TIER("QtyPaidOtherTier"),
    AMOUNT_PAID_OTHER_TIER("AmountPaidOtherTier");

    private final String columnName;

    LiITMSColumnEnum(String columnName) {
        this.columnName = columnName;
    }

    public static String[] getColumnNames() {
        return Arrays.stream(values())
            .map(LiITMSColumnEnum::getColumnName)
            .toArray(String[]::new);
    }
}
