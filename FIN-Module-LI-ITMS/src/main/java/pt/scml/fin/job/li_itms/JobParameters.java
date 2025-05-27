package pt.scml.fin.job.li_itms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "")
@Getter
@Setter
public class JobParameters {

    /**
     * if no date is passed as parameter procDate will assume current date - 1
     */
    private String procDate;

}
