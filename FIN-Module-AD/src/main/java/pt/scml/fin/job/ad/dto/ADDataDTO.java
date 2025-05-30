package pt.scml.fin.job.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ADDataDTO {

    private String recordType;
    private String totalBetSlips;
    private String totalBets;
    private String totalSalesAmount1;
    private String totalSalesAmount2;
    private String totalRefunds;
    private String totalRefundsAmount1;
    private String totalRefundsAmount2;
    private String totalPrizesPaid150;
    private String totalPrizesAmountPaid150;
    private String totalPrizesAmountPaid;
    private String settlementID;
    private String settlementDate;
    private String channel;
    private String salesCode;
    private String agentCode;

    public String last(String str, int nLastCharacters) {
        if (str == null) {
            return "";
        }
        if (str.length() < nLastCharacters) {
            return str;
        }
        return str.substring(str.length() - nLastCharacters);
    }

    public boolean isHeader() {
        return "HW1".equalsIgnoreCase(recordType);
    }

    public boolean isTrailer() {
        return "TP".equalsIgnoreCase(recordType);
    }

}

 