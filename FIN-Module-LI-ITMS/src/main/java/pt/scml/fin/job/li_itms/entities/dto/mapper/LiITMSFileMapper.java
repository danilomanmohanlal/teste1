package pt.scml.fin.job.li_itms.entities.dto.mapper;

import java.math.BigDecimal;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSColumnEnum;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;

public class LiITMSFileMapper implements FieldSetMapper<LiITMSFileDTO> {

    @Override
    public LiITMSFileDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        LiITMSFileDTO dto = new LiITMSFileDTO();

        dto.setStartDate(fieldSet.readString(LiITMSColumnEnum.START_DATE.getColumnName()));
        dto.setEndDate(fieldSet.readString(LiITMSColumnEnum.END_DATE.getColumnName()));
        dto.setGameNumber(fieldSet.readString(LiITMSColumnEnum.GAME_NUMBER.getColumnName()));
        dto.setTerminalNumber(
            fieldSet.readString(LiITMSColumnEnum.TERMINAL_NUMBER.getColumnName()));
        dto.setEmissionNumber(
            fieldSet.readString(LiITMSColumnEnum.EMISSION_NUMBER.getColumnName()));
        dto.setQtyOfBooks(fieldSet.readLong(LiITMSColumnEnum.QTY_OF_BOOKS.getColumnName()));
        dto.setAmountOfBooks(
            new BigDecimal(fieldSet.readString(LiITMSColumnEnum.AMOUNT_OF_BOOKS.getColumnName())));
        dto.setQtyPaidFirstTier(
            fieldSet.readLong(LiITMSColumnEnum.QTY_PAID_FIRST_TIER.getColumnName()));
        dto.setAmountPaidFirstTier(new BigDecimal(
            fieldSet.readString(LiITMSColumnEnum.AMOUNT_PAID_FIRST_TIER.getColumnName())));
        dto.setQtyPaidOtherTier(
            fieldSet.readLong(LiITMSColumnEnum.QTY_PAID_OTHER_TIER.getColumnName()));
        dto.setAmountPaidOtherTier(new BigDecimal(
            fieldSet.readString(LiITMSColumnEnum.AMOUNT_PAID_OTHER_TIER.getColumnName())));

        return dto;
    }
}
