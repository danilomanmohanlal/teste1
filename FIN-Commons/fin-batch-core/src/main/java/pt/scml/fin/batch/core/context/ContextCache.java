package pt.scml.fin.batch.core.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ContextCache implements Serializable {

    private Long channelId;
    private String gameId;

    /**
     * Key - filetype
     * value - filename
     */
    private Map<String, String> fileMap = new HashMap<>();
    private String inputDirectory;
    private String workDirectory;
    private String errorDirectory;
    private String duplicatedDirectory;
    private String successDirectory;

}