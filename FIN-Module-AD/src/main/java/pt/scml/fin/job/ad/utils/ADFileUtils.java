package pt.scml.fin.job.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.batch.core.utils.FileUtils;

@Slf4j
@Service
public class ADFileUtils extends FileUtils {

    public ADFileUtils(ContextHeader contextHeader, ContextCache contextCache, FinUtilsService finUtilsService) {
        super(contextHeader, contextCache, finUtilsService);
    }

    @Override
    public String getFilePattern() {
        return "^INVABP_YYYYMMDD\\.[Aa][Ss][Cc]$";
    }

    @Override
    public String getHeaderType() {
        return "HW1";
    }

    @Override
    public int getInitialSubStringDate() {
        return 3;
    }

    @Override
    public int getFinalSubStringDate() {
        return 11;
    }

    @Override
    public int getInitialTrailerCount() {
        return 2;
    }

    @Override
    public int getFinalTrailerCount() {
        return 10;
    }

}
