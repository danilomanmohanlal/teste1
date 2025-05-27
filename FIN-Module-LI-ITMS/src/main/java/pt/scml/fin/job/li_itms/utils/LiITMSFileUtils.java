package pt.scml.fin.job.li_itms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;

@Slf4j
@Service
public class LiITMSFileUtils {

    public static final String FILE_PATTERN = "(?i)^INVITMS_%s\\.CSV$";
    private final ContextHeader contextHeader;
    private final ContextCache contextCache;
    private File file;

    public LiITMSFileUtils(ContextHeader contextHeader,
        ContextCache contextCache) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    public String hasFileToProcess() throws IOException {
        File matchedFile = getMatchedFile();
        validateFileContents();
        return matchedFile.getName();
    }

    private File getMatchedFile() throws FileNotFoundException {
        file = new File(contextCache.getInputDirectory());
        final Pattern pattern = Pattern.compile("(?i)^INVITMS_\\d{8}\\.CSV$");

        FilenameFilter filter = (dir, name) -> pattern.matcher(name).matches();
        String[] matchingFilenames = file.list(filter);

        if (matchingFilenames == null || matchingFilenames.length == 0) {
            throw new FileNotFoundException("No file matching pattern 'INVITMS_YYYYMMDD.CSV' found in: " + contextCache.getInputDirectory());
        } else if (matchingFilenames.length > 1) {
            throw new IllegalStateException("More than one file found matching pattern in path: " + contextCache.getInputDirectory()
                + " -> " + Arrays.toString(matchingFilenames));
        }
        contextHeader.setFilename(matchingFilenames[0]);

        file = new File(file, matchingFilenames[0]);

        return file;
    }

    private void validateFileContents() {
        validateProcDate();
    }

    private void validateProcDate() {
        if(!file.getName().contains(contextHeader.getProcDate())){
            throw new IllegalStateException("File Date does not match with procDate(processDate).");
        }
    }

}
