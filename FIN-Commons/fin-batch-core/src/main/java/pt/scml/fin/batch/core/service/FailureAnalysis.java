package pt.scml.fin.batch.core.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.exceptions.JobWithModuleIdInExecutionException;

@Component
@Getter
@Setter
@Slf4j
public class FailureAnalysis {

    private boolean duplicateFile;
    private String errorCode;
    private String errorMessage;
    
    public static FailureAnalysis analyze(JobExecution jobExecution) {
        FailureAnalysis analysis = new FailureAnalysis();
        
        for (Throwable exception : jobExecution.getFailureExceptions()) {
            if (exception instanceof JobWithModuleIdInExecutionException) {
                analysis.setDuplicateFile(true);
                analysis.setErrorCode("1");
                analysis.setErrorMessage("File already processed");
            }
            // ... other failure types
        }
        
        return analysis;
    }
    
    // getters/setters
}