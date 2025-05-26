package pt.scml.fin.batch.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import pt.scml.fin.batch.core.exceptions.CreateDirectoryException;
import pt.scml.fin.batch.core.exceptions.MoveFileException;

@Slf4j
public class FinUtils {

    private FinUtils() {
    }

    /**
     * Static method that will try to move the source path to the target path
     *
     * @param sourceFolder the original path
     * @param targetFolder the destination path
     */
    public static void moveFileTo(String sourceFolder, String targetFolder, String filename) {
        try {
            Path sourcePath = Paths.get(sourceFolder + filename);
            Path targetPath = Paths.get(targetFolder + filename);

            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Moved " + filename + " to " + targetFolder);
        } catch (IOException ex) {
            log.error("{} Failed to move the file", ex.getMessage());
            throw new MoveFileException(
                "Failed to move the file from " + sourceFolder + " to " + targetFolder, ex);
        }
    }

    /***
     * Static method that it's going to check if the directory already exists, if not it will create.
     *
     * @param directory the directory
     */
    public static void createDirectoryIfNotExists(String directory) {
        try {
            log.debug("Checking if the path " + directory + " exists..");
            Path directoryPath = Path.of(directory);
            if (!Files.exists(directoryPath)) {
                log.info("The path " + directory + " does not exists, its going to create..");
                Files.createDirectories(directoryPath);
                log.info("The path " + directory + " was created successfully.");
            } else {
                log.debug("The path " + directory + " already exists.");
            }
        } catch (IOException ex) {
            log.error("Error on checking if the folder " + directory + " exists or creating.");
            throw new CreateDirectoryException("Failed to create directory " + directory, ex);
        }
    }
}
