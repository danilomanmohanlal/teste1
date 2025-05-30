package pt.scml.fin.batch.core.context;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ContextCache implements Serializable {

    private Long channelId;
    private String gameId;

    /* System Directory's */
    private String inputDirectory;
    private String workDirectory;
    private String errorDirectory;
    private String duplicatedDirectory;
    private String successDirectory;

}