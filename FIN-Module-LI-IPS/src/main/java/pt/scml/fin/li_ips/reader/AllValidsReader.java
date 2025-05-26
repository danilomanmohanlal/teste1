package pt.scml.fin.li_ips.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.core.io.FileSystemResource;
import pt.scml.fin.li_ips.model.dto.LiDetailsValid;

@Slf4j
public class AllValidsReader {

    private final String filePath;

    public AllValidsReader(String filePath) {
        this.filePath = filePath;

    }

    public FlatFileItemReader<LiDetailsValid> reader() {
        FlatFileItemReader<LiDetailsValid> reader = new FlatFileItemReader<>();
        reader.setName("LiDetailsValidReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setLineMapper(new LiDetailsValidLineMapper());
        return reader;
    }

    static class LiDetailsValidLineMapper implements LineMapper<LiDetailsValid> {

        @Override
        public LiDetailsValid mapLine(String line, int lineNumber) throws Exception {
            if (line.length() != 118) {
                throw new IllegalArgumentException("Line " + lineNumber + " has invalid length: " + line.length());
            }

            // Optional: add numeric check for the first line (legacy behavior)
            if (lineNumber == 1) {
                try {
                    Double.parseDouble(line);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Line " + lineNumber + " is expected to be numeric", e);
                }
            }

            return new LiDetailsValid(line);
        }
    }
}