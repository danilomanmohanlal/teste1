//package pt.scml.fin.li_ips.reader;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileParseException;
//import org.springframework.batch.item.file.LineMapper;
//import org.springframework.core.io.FileSystemResource;
//import pt.scml.fin.li_ips.model.dto.LiDetails;
//
//@Slf4j
//public class AllSalesReader {
//
//    private final String filePath;
//
//    public AllSalesReader(String filePath) {
//        this.filePath = filePath;
//    }
//
//    public FlatFileItemReader<LiDetails> reader() {
//        FlatFileItemReader<LiDetails> reader = new FlatFileItemReader<>();
//        reader.setResource(new FileSystemResource(filePath));
//        reader.setLineMapper(new AllSalesLineMapper());
//        return reader;
//    }
//
//    static class AllSalesLineMapper implements LineMapper<LiDetails> {
//
//        @Override
//        public LiDetails mapLine(String line, int lineNumber) throws Exception {
//            if (line.length() != 52) {
//                throw new FlatFileParseException("Invalid line length at line " + lineNumber, line,
//                        lineNumber);
//            }
//
//            // Only validate line 1 is numeric
//            if (lineNumber == 1 && !line.matches("\\d+")) {
//                throw new FlatFileParseException("First line must be numeric", line, lineNumber);
//            }
//
//            return new LiDetails(line); // pass null if logFile not needed
//        }
//    }
//
//}
