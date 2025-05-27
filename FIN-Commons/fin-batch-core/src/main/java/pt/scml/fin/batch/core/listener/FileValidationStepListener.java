//package pt.scml.fin.batch.core.listener;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.ExitStatus;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.StepExecutionListener;
//import org.springframework.stereotype.Component;
//import pt.scml.fin.batch.core.context.ContextCache;
//import pt.scml.fin.batch.core.context.ContextHeader;
//import pt.scml.fin.batch.core.service.ControlService;
//import pt.scml.fin.batch.core.utils.FinUtils;
//
//@Slf4j
//@Component
//public class FileValidationStepListener implements StepExecutionListener {
//
//    private final ControlService controlService;
//    private final ContextHeader contextHeader;
//    private final ContextCache contextCache;
//
//    public FileValidationStepListener(ControlService controlService,
//                                    ContextHeader contextHeader,
//                                    ContextCache contextCache) {
//        this.controlService = controlService;
//        this.contextHeader = contextHeader;
//        this.contextCache = contextCache;
//    }
//
//    @Override
//    public void beforeStep(StepExecution stepExecution) {
//        String currentFilename = contextHeader.getFilename();
//
//        if (currentFilename == null) {
//            log.warn("No filename set in context header");
//            return;
//        }
//
//        log.info("Validating file: {}", currentFilename);
//
//        // Check if this specific file is already processed
//        boolean isAlreadyProcessed = controlService.isFileAlreadyProcessed(currentFilename);
//
//        if (isAlreadyProcessed) {
//            log.warn("File {} is already processed, moving to duplicate folder", currentFilename);
//
//            // Move file to duplicate folder
//            FinUtils.moveFileTo(
//                contextCache.getWorkDirectory(),
//                contextCache.getDuplicatedDirectory(),
//                currentFilename
//            );
//
//            // Mark step as skipped - this will skip processing but won't fail the job
//            stepExecution.setExitStatus(new ExitStatus("SKIPPED", "File already processed"));
//            stepExecution.setTerminateOnly(); // Skip this step gracefully
//        }
//    }
//
//    @Override
//    public ExitStatus afterStep(StepExecution stepExecution) {
//        if ("SKIPPED".equals(stepExecution.getExitStatus().getExitCode())) {
//            log.info("Step {} was skipped due to duplicate file", stepExecution.getStepName());
//            return new ExitStatus("SKIPPED");
//        }
//        return stepExecution.getExitStatus();
//    }
//}