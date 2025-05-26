package pt.scml.fin.li_ips.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LIGameDetails {
    private String gameNumber;
    private String orderRequest;
    private String orderValue;
}
