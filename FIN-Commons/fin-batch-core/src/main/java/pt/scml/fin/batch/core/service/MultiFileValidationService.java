package pt.scml.fin.batch.core.service;

import static pt.scml.fin.batch.core.utils.FinUtils.createDirectoryIfNotExists;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.FinUtils;
import pt.scml.fin.model.dto.enums.CtrlFileStatusEnum;
import pt.scml.fin.model.dto.enums.CtrlProcessStatusEnum;

@Service
@Slf4j
public class MultiFileValidationService {

    private final ControlService controlService;
    private final ContextCache contextCache;
    private final FinUtilsService finUtilsService;
    private final ContextHeader contextHeader;
    //private final FailureAnalysis failureAnalysis;

    public MultiFileValidationService(ControlService controlService, ContextCache contextCache,
            FinUtilsService finUtilsService, ContextHeader contextHeader) {
        this.controlService = controlService;
        this.contextCache = contextCache;
        this.finUtilsService = finUtilsService;
        this.contextHeader = contextHeader;
//        this.failureAnalysis = failureAnalysis;
    }


    private static final String CONFIG_HOME = "HOME";
    private static final String CONFIG_INPUT = "INPUT";
    private static final String CONFIG_ERROR = "ERROR";
    private static final String CONFIG_SUCCESS = "SUCCESS";
    private static final String CONFIG_WORK = "WORK";
    private static final String CONFIG_DUPLICATED = "DUPLICATED";

    public void loadDirectoryCacheData(String jobFolder) {

        String homePath = finUtilsService.getConfigValue(CONFIG_HOME);
        String inputFolder = finUtilsService.getConfigValue(CONFIG_INPUT);
        String errorFolder = finUtilsService.getConfigValue(CONFIG_ERROR);
        String successFolder = finUtilsService.getConfigValue(CONFIG_SUCCESS);
        String workFolder = finUtilsService.getConfigValue(CONFIG_WORK);
        String duplicatedFolder = finUtilsService.getConfigValue(CONFIG_DUPLICATED);

        String inputDirectory = homePath + jobFolder + inputFolder;
        createDirectoryIfNotExists(inputDirectory);
        contextCache.setInputDirectory(inputDirectory);

        String workDirectory = homePath + jobFolder + workFolder;
        createDirectoryIfNotExists(workDirectory);
        contextCache.setWorkDirectory(workDirectory);

        String successDirectory = homePath + jobFolder + successFolder;
        createDirectoryIfNotExists(successDirectory);
        contextCache.setSuccessDirectory(successDirectory);

        String errorDirectory = homePath + jobFolder + errorFolder;
        createDirectoryIfNotExists(errorDirectory);
        contextCache.setErrorDirectory(errorDirectory);

        String duplicatedDirectory = homePath + jobFolder + duplicatedFolder;
        createDirectoryIfNotExists(duplicatedDirectory);
        contextCache.setDuplicatedDirectory(duplicatedDirectory);

    }

    public boolean validateFileExists() {
        File file = new File(contextCache.getInputDirectory());
        final Pattern pattern = Pattern.compile(contextHeader.getFilePattern());

        FilenameFilter filter = (dir, name) -> pattern.matcher(name).matches();
        String[] matchingFilenames = file.list(filter);

//        if (matchingFilenames == null || matchingFilenames.length == 0) {
//            throw new FileNotFoundException("No file matching pattern 'INVITMS_YYYYMMDD.CSV' found in: " + contextCache.getInputDirectory());
//        } else if (matchingFilenames.length > 1) {
//            throw new IllegalStateException("More than one file found matching pattern in path: " + contextCache.getInputDirectory()
//                    + " -> " + Arrays.toString(matchingFilenames));
//        }

        if (matchingFilenames.length == 0) {
//            failureAnalysis.setProcessStatusEnum(CtrlProcessStatusEnum.ERROR);
//            failureAnalysis.setFileStatusEnum(CtrlFileStatusEnum.ERROR);
//            failureAnalysis.setFailed(true);
            return false;
        }

        contextHeader.setFilename(matchingFilenames[0]);

        return true;
    }

    /**
     * Generic multi-file validation that can be used by ANY job Validates all files in the fileMap
     * and returns which ones should be processed
     *
     * @param fileMap Map of fileKey -> filename to validate
     * @return Map of fileKey -> shouldProcess (true if file should be processed)
     */
//    public Map<String, Boolean> validateFiles(Map<String, String> fileMap) {
//        Map<String, Boolean> validationResults = new HashMap<>();
//
//        log.info("Starting multi-file validation for {} files", fileMap.size());
//
//        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
//            String fileKey = entry.getKey();
//            String filename = entry.getValue();
//
//            log.info("Validating file: {} ({})", fileKey, filename);
//
//            try {
//                boolean isAlreadyProcessed = controlService.isFileAlreadyProcessed(filename);
//
//                if (isAlreadyProcessed) {
//                    log.warn("File {} is already processed, moving to duplicate folder", filename);
//
//                    // Move file from INPUT to DUPLICATED
//                    FinUtils.moveFileTo(
//                            contextCache.getInputDirectory(),
//                            contextCache.getDuplicatedDirectory(),
//                            filename
//                    );
//
//                    validationResults.put(fileKey, false); // Don't process
//                } else {
//                    log.info("File {} is ready for processing", filename);
//                    validationResults.put(fileKey, true); // Process this file
//                }
//
//            } catch (Exception e) {
//                log.error("Error validating file {}: {}", filename, e.getMessage());
//
//                // Move problematic files to duplicate folder (safe approach)
//                try {
//                    FinUtils.moveFileTo(
//                            contextCache.getInputDirectory(),
//                            contextCache.getDuplicatedDirectory(),
//                            filename
//                    );
//                } catch (Exception moveException) {
//                    log.error("Failed to move problematic file {}: {}", filename,
//                            moveException.getMessage());
//                }
//
//                validationResults.put(fileKey, false); // Don't process problematic files
//            }
//        }
//
//        log.info("Multi-file validation completed. Results: {}", validationResults);
//        return validationResults;
//    }

    public boolean validateSingleFile(String filename) {
//        try {
//            boolean fileAlreadyProcessed = controlService.isFileAlreadyProcessed(filename);
//            if (fileAlreadyProcessed) {
//                failureAnalysis.setProcessStatusEnum(CtrlProcessStatusEnum.ERROR);
//                failureAnalysis.setFileStatusEnum(CtrlFileStatusEnum.DUPLICATE);
//                failureAnalysis.setDuplicated(true);
//                failureAnalysis.setFailed(true);
//            }
//
//            return !fileAlreadyProcessed;
//        } catch (Exception e) {
//            log.error("Error validating single file {}: {}", filename, e.getMessage());
//            return false;
//        }

        return !controlService.isFileAlreadyProcessed(filename);

    }


//    public boolean hasFilesToProcess(Map<String, Boolean> validationResults) {
//        return validationResults.values().stream().anyMatch(Boolean::booleanValue);
//    }
//
//
//    public Set<String> getFilesToProcess(Map<String, Boolean> validationResults) {
//        return validationResults.entrySet().stream()
//                .filter(Map.Entry::getValue)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//    }
//
//    public Set<String> getDuplicateFiles(Map<String, Boolean> validationResults) {
//        return validationResults.entrySet().stream()
//                .filter(entry -> !entry.getValue())
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//    }
}