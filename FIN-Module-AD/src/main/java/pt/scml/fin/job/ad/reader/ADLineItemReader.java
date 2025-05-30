package pt.scml.fin.job.ad.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.job.ad.dto.ADDataDTO;

@Component
public class ADLineItemReader {

    private final ContextHeader contextHeader;
    private final ContextCache contextCache;

    public ADLineItemReader(ContextHeader contextHeader, ContextCache contextCache) {
        this.contextHeader = contextHeader;
        this.contextCache = contextCache;
    }

    public FlatFileItemReader<ADDataDTO> reader() {
        return new FlatFileItemReaderBuilder<ADDataDTO>()
            .name("ADData")
            .resource(new FileSystemResource(
                contextCache.getWorkDirectory() + contextHeader.getFilename()))
            .lineMapper((line, lineNumber) -> {

                ADDataDTO data = new ADDataDTO();

                // Header line
                if (line.startsWith("HW1")) {
                    data.setRecordType("HW1");
                    return data;
                }

                // Trailer line
                if (line.startsWith("TP")) {
                    data.setRecordType("TP");
                    return data;
                }

                // Data lines
                data.setRecordType(line.substring(0, 2));
                data.setTotalBetSlips(line.substring(2, 11));
                data.setTotalBets(line.substring(11, 20));
                data.setTotalSalesAmount1(line.substring(20, 31));
                data.setTotalSalesAmount2(line.substring(31, 36));
                data.setTotalRefunds(line.substring(36, 45));
                data.setTotalRefundsAmount1(line.substring(45, 56));
                data.setTotalRefundsAmount2(line.substring(56, 61));
                data.setTotalPrizesPaid150(line.substring(61, 70));
                data.setTotalPrizesAmountPaid150(line.substring(70, 81));
                data.setTotalPrizesAmountPaid(line.substring(81, 86));
                data.setSettlementID(line.substring(86, 96));
                data.setSettlementDate(line.substring(96, 104));
                data.setChannel(line.substring(104, 106));
                data.setSalesCode(line.substring(106, 112));
                data.setAgentCode(line.substring(112, 119));

                return data;

            })
            .build();
    }
}