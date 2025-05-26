package pt.scml.fin.li_ips.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class IPSOrderConfirmationDTO {

    private String recordType;
    private String agentCode;
    private String firmCode;
    private String packageNumber;
    private List<LIGameDetails> gameDetails;
    private String fileDate;
    private String totalRecordsBody;
    private String fileHeader;
    private String fileTrailer;
}
