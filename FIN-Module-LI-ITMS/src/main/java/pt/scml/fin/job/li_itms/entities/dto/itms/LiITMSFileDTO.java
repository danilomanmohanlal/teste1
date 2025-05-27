package pt.scml.fin.job.li_itms.entities.dto.itms;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class LiITMSFileDTO {

    private String startDate;
    private String endDate;
    private String gameNumber;
    private String terminalNumber;
    private String emissionNumber;
    private Long qtyOfBooks;
    private BigDecimal amountOfBooks;
    private Long qtyPaidFirstTier;
    private BigDecimal amountPaidFirstTier;
    private Long qtyPaidOtherTier;
    private BigDecimal amountPaidOtherTier;

}