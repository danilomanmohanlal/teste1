package pt.scml.fin.li_ips.utils;

import static pt.scml.fin.li_ips.JobParameters.IPS_ORDER_CONFIRMATION;
import static pt.scml.fin.li_ips.JobParameters.SCML_ALL_FILES;
import static pt.scml.fin.li_ips.JobParameters.SCML_ALL_SALES;
import static pt.scml.fin.li_ips.JobParameters.SCML_ALL_VALIDS;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.li_ips.JobParameters;

@Component
@Slf4j
public class LIInvoicingParameterValidator {

    private final ContextHeader contextHeader;

    public LIInvoicingParameterValidator(ContextHeader contextHeader) {
        this.contextHeader = contextHeader;
    }

    public void validate(JobParameters parameters) {
        log.info("Validating job parameters...");

        String inputParam = parameters.getFileType();
        switch (inputParam) {
            case IPS_ORDER_CONFIRMATION -> log.info("Processing file IPS_ORDER_CONFIRMATION");
            case SCML_ALL_VALIDS -> log.info("Processing file SCML_ALL_VALIDS");
            case SCML_ALL_SALES -> log.info("Processing file SCML_ALL_SALES");
            case SCML_ALL_FILES -> log.info("Processing all files");
            default -> {
                String msg = "Valid options are:      * Import options:\n"
                        + "     *    <-s> -> Importa o ficheiro SCML_ALL_SALES\n"
                        + "     *    <-v> -> Importa o ficheiro SCML_ALL_VALIDS\n"
                        + "     *    <-a> -> Importa todos os ficheiros\n"
                        + "     *    <-i> -> Importa o ficheiro IPS_ORDER_CONFIRMATION";

                contextHeader.setJobParamsValid(false);
                contextHeader.setParamsExceptionMessage("Unsupported file type: " + inputParam + " " + msg);
                log.error("Unsupported file type: {} \n valid options are {}", inputParam, msg);
            }

        }
    }

}