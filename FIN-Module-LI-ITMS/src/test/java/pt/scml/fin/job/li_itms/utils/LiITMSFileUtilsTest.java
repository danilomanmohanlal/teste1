package pt.scml.fin.job.li_itms.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiITMSFileUtilsTest {

    private ContextCache contextCache;
    private ContextHeader contextHeader;
    private LiITMSFileUtils fileUtils;

    private final String mockDirectory = "test/stubs/";

    @BeforeEach
    void setUp() {
        contextCache = mock(ContextCache.class);
        contextHeader = mock(ContextHeader.class);

        when(contextCache.getInputDirectory()).thenReturn(mockDirectory);
        when(contextHeader.getProcDate()).thenReturn("20220703");

        fileUtils = new LiITMSFileUtils(contextHeader, contextCache);
    }

    @Test
    void testHasFileToProcess_successful() throws IOException {
        File dir = new File(mockDirectory);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "INVITMS_20220703.csv");
        file.createNewFile();
        file.deleteOnExit();

        String result = fileUtils.hasFileToProcess();
        assertThat(result).isEqualTo("INVITMS_20220703.csv");
        verify(contextHeader).setFilename("INVITMS_20220703.csv");
    }

    @Test
    void testHasFileToProcess_fileNotFound() {
        File dir = new File(mockDirectory);
        for (File f : dir.listFiles()) f.delete();

        assertThatThrownBy(() -> fileUtils.hasFileToProcess())
            .isInstanceOf(FileNotFoundException.class)
            .hasMessageContaining("No file matching pattern");
    }

    @Test
    void testHasFileToProcess_multipleFilesFound() throws IOException {
        File dir = new File(mockDirectory);
        dir.mkdirs();

        File f1 = new File(dir, "INVITMS_20220703.csv");
        File f2 = new File(dir, "INVITMS_20220704.csv");
        f1.createNewFile();
        f2.createNewFile();
        f1.deleteOnExit();
        f2.deleteOnExit();

        assertThatThrownBy(() -> fileUtils.hasFileToProcess())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("More than one file found matching pattern");
    }

    @Test
    void testHasFileToProcess_procDateMismatch() throws IOException {
        when(contextHeader.getProcDate()).thenReturn("20220101");

        File dir = new File(mockDirectory);
        dir.mkdirs();

        File file = new File(dir, "INVITMS_20220703.csv");
        file.createNewFile();
        file.deleteOnExit();

        assertThatThrownBy(() -> fileUtils.hasFileToProcess())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("File Date does not match with procDate");
    }
}
