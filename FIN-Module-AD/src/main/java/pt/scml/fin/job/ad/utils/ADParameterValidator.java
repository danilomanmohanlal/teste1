package pt.scml.fin.job.ad.utils;

import java.io.IOException;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.job.ad.JobParameters;

@Component
@Slf4j
public class ADParameterValidator {

    private static final String MODULE_SHDES = "ADINVOICINGMNG";
    private final ContextHeader contextHeader;

    public ADParameterValidator(ContextHeader contextHeader) {
        this.contextHeader = contextHeader;
    }

    public void validate(JobParameters parameters) {
        log.info("Validating job parameters..");
        String procDate = parameters.getProcDate();

        if (Strings.isEmpty(procDate)) {
            String date = DateUtils.getStringFromLocalDate(
                LocalDate.now().minusDays(1L), DateUtils.YYYYMMDD);
            log.info("No processing date provided, defaulting to previous day: {}", date);
            procDate = date;
        } else if (!DateUtils.isDateBeforeToday(procDate)) {
            String msg = String.format("Invalid processing date: %s, must be before current date",
                procDate);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            log.info("Using processing date: {}", procDate);
        }

        contextHeader.setProcDate(procDate);
        contextHeader.setModuleShdes(MODULE_SHDES);
        contextHeader.setJobHasAFile(true);

        log.info("Valid job parameters.");
    }

}