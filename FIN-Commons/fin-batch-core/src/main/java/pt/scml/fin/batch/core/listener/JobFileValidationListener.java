//package pt.scml.fin.batch.core.listener;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.stereotype.Component;
//import pt.scml.fin.batch.core.context.ContextHeader;
//import pt.scml.fin.batch.core.service.MultiFileValidationService;
//
//@Slf4j
//@Component
//public class JobFileValidationListener implements JobExecutionListener {
//
//    private final MultiFileValidationService multiFileValidationService;
//    private final ContextHeader contextHeader;
//
//    public JobFileValidationListener(MultiFileValidationService multiFileValidationService,
//            ContextHeader contextHeader) {
//        this.multiFileValidationService = multiFileValidationService;
//        this.contextHeader = contextHeader;
//    }
//
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//
//        //TODO: review the messages
//        boolean fileExists = this.multiFileValidationService.validateFileExists();
//        if (!fileExists)
//            throw new RuntimeException("FICHEIRO NAO EXISTE");
//
//        boolean validateSingleFile = this.multiFileValidationService.validateSingleFile(contextHeader.getFilename());
//        if (!validateSingleFile) {
//            contextHeader.setDuplicated(true);
//            throw new RuntimeException("FICHEIRO JA PROCESSADO " + contextHeader.getFilename());
//        }
//
//    }
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        //do nothing for now
//    }
//}
