package pt.scml.fin.batch.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.utils.FinUtils;

@Service
@Slf4j
public class MultiFileValidationService {
    
    private final ControlService controlService;
    private final ContextCache contextCache;
    
    public MultiFileValidationService(ControlService controlService, ContextCache contextCache) {
        this.controlService = controlService;
        this.contextCache = contextCache;
    }
    
    /**
     * Generic multi-file validation that can be used by ANY job
     * Validates all files in the fileMap and returns which ones should be processed
     * 
     * @param fileMap Map of fileKey -> filename to validate
     * @return Map of fileKey -> shouldProcess (true if file should be processed)
     */
    public Map<String, Boolean> validateFiles(Map<String, String> fileMap) {
        Map<String, Boolean> validationResults = new HashMap<>();
        
        log.info("Starting multi-file validation for {} files", fileMap.size());
        
        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
            String fileKey = entry.getKey();
            String filename = entry.getValue();
            
            log.info("Validating file: {} ({})", fileKey, filename);
            
            try {
                boolean isAlreadyProcessed = controlService.isFileAlreadyProcessed(filename);
                
                if (isAlreadyProcessed) {
                    log.warn("File {} is already processed, moving to duplicate folder", filename);
                    
                    // Move file from INPUT to DUPLICATED
                    FinUtils.moveFileTo(
                        contextCache.getInputDirectory(),
                        contextCache.getDuplicatedDirectory(),
                        filename
                    );
                    
                    validationResults.put(fileKey, false); // Don't process
                } else {
                    log.info("File {} is ready for processing", filename);
                    validationResults.put(fileKey, true); // Process this file
                }
                
            } catch (Exception e) {
                log.error("Error validating file {}: {}", filename, e.getMessage());
                
                // Move problematic files to duplicate folder (safe approach)
                try {
                    FinUtils.moveFileTo(
                        contextCache.getInputDirectory(),
                        contextCache.getDuplicatedDirectory(),
                        filename
                    );
                } catch (Exception moveException) {
                    log.error("Failed to move problematic file {}: {}", filename, moveException.getMessage());
                }
                
                validationResults.put(fileKey, false); // Don't process problematic files
            }
        }
        
        log.info("Multi-file validation completed. Results: {}", validationResults);
        return validationResults;
    }
    
    /**
     * Convenience method for single-file validation (maintains backward compatibility)
     * Existing single-file jobs can still use this
     */
    public boolean validateSingleFile(String filename) {
        try {
            return !controlService.isFileAlreadyProcessed(filename);
        } catch (Exception e) {
            log.error("Error validating single file {}: {}", filename, e.getMessage());
            return false;
        }
    }
    
    /**
     * Utility method to check if any files passed validation
     * Useful for determining if job should continue
     */
    public boolean hasFilesToProcess(Map<String, Boolean> validationResults) {
        return validationResults.values().stream().anyMatch(Boolean::booleanValue);
    }
    
    /**
     * Get list of file keys that passed validation
     */
    public Set<String> getFilesToProcess(Map<String, Boolean> validationResults) {
        return validationResults.entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get list of file keys that were duplicates
     */
    public Set<String> getDuplicateFiles(Map<String, Boolean> validationResults) {
        return validationResults.entrySet().stream()
            .filter(entry -> !entry.getValue())
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }
}