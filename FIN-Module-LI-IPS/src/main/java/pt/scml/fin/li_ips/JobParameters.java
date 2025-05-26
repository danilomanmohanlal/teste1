package pt.scml.fin.li_ips;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "")
@Getter
@Setter
public class JobParameters {

    public static final String IPS_ORDER_CONFIRMATION = "i";
    public static final String SCML_ALL_SALES = "s";
    public static final String SCML_ALL_VALIDS = "v";
    public static final String SCML_ALL_FILES = "a";

    /**
     * Import options:
     *    <-s> -> Importa o ficheiro SCML_ALL_SALES
     *    <-v> -> Importa o ficheiro SCML_ALL_VALIDS
     *    <-a> -> Importa todos os ficheiros
     *    <-i> -> Importa o ficheiro IPS_ORDER_CONFIRMATION
     */
    private String fileType;
}
