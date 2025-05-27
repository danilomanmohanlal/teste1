package pt.scml.fin.job.li_itms.reader;

import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSColumnEnum;
import pt.scml.fin.job.li_itms.entities.dto.itms.LiITMSFileDTO;
import pt.scml.fin.job.li_itms.entities.dto.mapper.LiITMSFileMapper;

@Slf4j
public class LiITMSFileReader {

    private final ContextCache contextCache;
    private final ContextHeader contextHeader;

    public LiITMSFileReader(ContextCache contextCache, ContextHeader contextHeader) {
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
    }

    public ItemReader<LiITMSFileDTO> reader() {
        String filePath = Paths.get(
            contextCache.getWorkDirectory(),
            contextHeader.getFilename()
        ).toString();

        log.info("Reading ITMS file from: {}", filePath);

        return new FlatFileItemReaderBuilder<LiITMSFileDTO>()
            .name("liITMSItemReader")
            .resource(new FileSystemResource(filePath))
            .delimited()
            .delimiter(";")
            .names(
                LiITMSColumnEnum.START_DATE.getColumnName(),
                LiITMSColumnEnum.END_DATE.getColumnName(),
                LiITMSColumnEnum.GAME_NUMBER.getColumnName(),
                LiITMSColumnEnum.TERMINAL_NUMBER.getColumnName(),
                LiITMSColumnEnum.EMISSION_NUMBER.getColumnName(),
                LiITMSColumnEnum.QTY_OF_BOOKS.getColumnName(),
                LiITMSColumnEnum.AMOUNT_OF_BOOKS.getColumnName(),
                LiITMSColumnEnum.QTY_PAID_FIRST_TIER.getColumnName(),
                LiITMSColumnEnum.AMOUNT_PAID_FIRST_TIER.getColumnName(),
                LiITMSColumnEnum.QTY_PAID_OTHER_TIER.getColumnName(),
                LiITMSColumnEnum.AMOUNT_PAID_OTHER_TIER.getColumnName()
            )
            .fieldSetMapper(new LiITMSFileMapper())
            .linesToSkip(1)
            .build();
    }
}
