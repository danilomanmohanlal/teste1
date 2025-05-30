package pt.scml.fin.batch.core.context;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ContextHeader implements Serializable {

    private String userId; //job name
    private Long numberOfRecords;
    private List<Class<?>> entitiesToRollback;
    private String filename;
    private String procDate;
    private String moduleShdes;
    private Long moduleId;
    private Long currFinPeriodId;
    private Long controlProcessId;
    private Long jobExecutionId;
    private boolean jobHasAFile;
    private boolean isDuplicated;

    private boolean isJobParamsValid = true;
    private String paramsExceptionMessage;
    private String jobFolder;
    private String filePattern;

    //ERROR or Exception
    private boolean hasFileError;
    private String fileErrorMessage;

}
