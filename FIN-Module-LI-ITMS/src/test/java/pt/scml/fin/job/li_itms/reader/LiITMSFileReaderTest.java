package pt.scml.fin.job.li_itms.reader;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.FileSystemResource;

import org.springframework.core.io.Resource;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;

import static org.assertj.core.api.Assertions.assertThat;

class LiITMSFileReaderTest {

    private ContextCache contextCache;
    private ContextHeader contextHeader;

    private LiITMSFileReader fileReader;

    @BeforeEach
    void setup() {
        contextCache = Mockito.mock(ContextCache.class);
        contextHeader = Mockito.mock(ContextHeader.class);

        Mockito.when(contextCache.getWorkDirectory()).thenReturn("test/stubs/");
        Mockito.when(contextHeader.getFilename()).thenReturn("INVITMS_20220703.csv");

        fileReader = new LiITMSFileReader(contextCache, contextHeader);
    }

    @Test
    void testReader_shouldCreateReaderWithCorrectPathAndSettings() throws Exception {
        ItemReader<LiITMSFileDTO> reader = fileReader.reader();

        assertThat(reader).isInstanceOf(FlatFileItemReader.class);

        FlatFileItemReader<LiITMSFileDTO> flatReader = (FlatFileItemReader<LiITMSFileDTO>) reader;

        Field resourceField = FlatFileItemReader.class.getDeclaredField("resource");
        resourceField.setAccessible(true);

        Resource resource = (Resource) resourceField.get(flatReader);

        assertThat(resource).isInstanceOf(FileSystemResource.class);
        assertThat(resource.getFilename()).isEqualTo("INVITMS_20220703.csv");
        assertThat(resource.getURI().getPath()).contains("test/stubs/INVITMS_20220703.csv");
    }
}
