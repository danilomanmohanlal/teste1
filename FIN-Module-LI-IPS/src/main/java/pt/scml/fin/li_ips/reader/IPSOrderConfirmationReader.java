package pt.scml.fin.li_ips.reader;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.core.io.FileSystemResource;
import pt.scml.fin.li_ips.model.dto.LIGameDetails;
import pt.scml.fin.li_ips.model.dto.IPSOrderConfirmationDTO;

@Slf4j
public class IPSOrderConfirmationReader {

    private final String filePath;

    public IPSOrderConfirmationReader(String filePath) {
        this.filePath = filePath;
    }

    public FlatFileItemReader<IPSOrderConfirmationDTO> reader() {
        FlatFileItemReader<IPSOrderConfirmationDTO> reader = new FlatFileItemReader<>();
        reader.setName("IPSOrderConfirmationReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setLineMapper(new IPSOrderConfirmationLineMapper());
        return reader;
    }

    static class IPSOrderConfirmationLineMapper implements LineMapper<IPSOrderConfirmationDTO> {

        @Override
        public IPSOrderConfirmationDTO mapLine(String line, int lineNumber) throws Exception {
            String recordType = line.substring(0, 2);
            IPSOrderConfirmationDTO dto = new IPSOrderConfirmationDTO();
            dto.setRecordType(recordType);

            switch (recordType) {
                case "HA": // Header
                    dto.setFileDate(line.substring(2, 10));
                    dto.setFileHeader(line.substring(10, 783));
                    break;

                case "DT": // Data/body
                    dto.setAgentCode(line.substring(2, 9));
                    dto.setFirmCode(line.substring(9, 15));
                    dto.setPackageNumber(line.substring(15, 23));

                    List<LIGameDetails> gameDetails = new ArrayList<>();
                    for (int i = 23; i + 19 <= line.length(); i += 19) {
                        String gameNumber = line.substring(i, i + 4).trim();
                        String orderRequest = line.substring(i + 4, i + 10).trim();
                        String orderValue = line.substring(i + 10, i + 19).trim();

                        if (!gameNumber.isEmpty()) {
                            gameDetails.add(new LIGameDetails(gameNumber, orderRequest, orderValue));
                        }
                    }
                    dto.setGameDetails(gameDetails);
                    break;

                case "TA": // Trailer
                    dto.setTotalRecordsBody(line.substring(2, 8));
                    dto.setFileTrailer(line.substring(8, 783));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown record type at line " + lineNumber);
            }

            return dto;
        }
    }

}