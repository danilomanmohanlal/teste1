package pt.scml.fin.li_ips.tasklet;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import pt.scml.fin.batch.core.context.ContextCache;
import pt.scml.fin.batch.core.context.ContextHeader;

@Slf4j
public class ClearValues implements Tasklet {

    private final ContextCache contextCache;
    private final ContextHeader contextHeader;

    public ClearValues(ContextCache contextCache, ContextHeader contextHeader) {
        this.contextCache = contextCache;
        this.contextHeader = contextHeader;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {

        final Pattern PATTERN_VALIDS = Pattern.compile("(?i)^scml_all_valids_.*\\d{8}\\.fil$");

        File fInput = new File(contextCache.getInputDirectory());
        FilenameFilter filterIps = (dir, name) -> PATTERN_VALIDS.matcher(name).matches();
        String[] list = fInput.list(filterIps);
        if (list.length > 1) {
            log.error(
                    "sErrorMsg = \"ERRO Existem varios ficheiros do mesmo tipo na diretoria de INPUT para processar\";");
            //throw exception
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        File fil3 = new File(list[0]);
        contextHeader.setFilename(fil3.getName());
        String sExecuteDate = fil3.getAbsolutePath()
                .substring(fil3.getAbsolutePath().lastIndexOf("_") + 1,
                        fil3.getAbsolutePath().lastIndexOf("."));
        try {
            Date dExecuteDate = formatter.parse(sExecuteDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        contextHeader.setProcDate(sExecuteDate);

        return RepeatStatus.FINISHED;
    }
}
