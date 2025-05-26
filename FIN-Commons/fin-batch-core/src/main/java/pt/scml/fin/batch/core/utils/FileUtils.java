package pt.scml.fin.batch.core.utils;

import static pt.scml.fin.batch.core.utils.FinUtils.createDirectoryIfNotExists;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.service.FinUtilsService;

@Slf4j
@Service
public abstract class FileUtils {

    public abstract String getFilePattern();
    public abstract String getHeaderType();
    public abstract int getInitialSubStringDate();
    public abstract int getFinalSubStringDate();
    public abstract int getInitialTrailerCount();
    public abstract int getFinalTrailerCount();
    private final ContextHeader contextHeader;
    private final ContextCache contextCache;
    private final FinUtilsService finUtilsService;

    private static final String CONFIG_HOME = "HOME";
    private static final String CONFIG_INPUT = "INPUT";
    private static final String CONFIG_ERROR = "ERROR";
    private static final String CONFIG_SUCCESS = "SUCCESS";
    private static final String CONFIG_WORK = "WORK";
    private static final String CONFIG_DUPLICATED = "DUPLICATED";

    public FileUtils(ContextHeader contextHeader,
        ContextCache contextCache, FinUtilsService finUtilsService) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
        this.finUtilsService = finUtilsService;
    }

    public String hasFilesToProcess() throws IOException {
        File matchedFile = getMatchedFile();
        validateFileContents(matchedFile);
        return matchedFile.getName();
    }

    public File getMatchedFile() throws FileNotFoundException {
        File file = new File(contextCache.getInputDirectory());
        FilenameFilter filter = (dir, name) -> name.matches(
                getFilePattern().replace(DateUtils.YYYYMMDD_CAPS_LOCK, contextHeader.getProcDate()));
        String[] matchingFiles = file.list(filter);
        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new FileNotFoundException(
                "No matching files found for: " + contextHeader.getProcDate());
        }
        contextHeader.setFilename(matchingFiles[0]);

        file = new File(file, matchingFiles[0]);

        return file;
    }

    public void validateFileContents(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        if (lines.size() < 2) {
            throw new IllegalStateException("The file only has a header and trailer.");
        }
        validateHeaderDate(lines.getFirst());
        validateTrailerCount(lines.getLast(), lines.size());
    }

    public void validateHeaderDate(String header) {
        if (!header.startsWith(getHeaderType())) {
            throw new IllegalStateException("The file does not have a valid header.");
        }
        String headerDate = header.substring(getInitialSubStringDate(), getFinalSubStringDate());
        if (!headerDate.equals(contextHeader.getProcDate())) {
            throw new IllegalStateException("Header date does not match procDate.");
        }
    }

    public void validateTrailerCount(String trailer, int actualLines) {
         String trailerLine = trailer.substring(getInitialTrailerCount(), getFinalTrailerCount());
        try {
            int expectedLines = Integer.parseInt(trailerLine);
            if (actualLines != expectedLines) {
                throw new IllegalStateException("Line count does not match trailer.");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid number in trailer.", ex);
        }
    }

    /**
     * Aux method that checks if the folders of a job (input, error, success, work and duplicated)
     * are created, if not it creates the folders.
     *
     * @param jobFolder the job folder name
     */
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

}
