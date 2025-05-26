package pt.scml.fin.li_ips;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class StageDecider implements JobExecutionDecider {

    private final String inputParam;

    public StageDecider(String inputParam) {
        this.inputParam = inputParam;
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        return switch (inputParam) {
            case "IPS" -> new FlowExecutionStatus("IPS");
            case "ALL_VALIDS" -> new FlowExecutionStatus("ALL_VALIDS");
            case "SALES" -> new FlowExecutionStatus("SALES");
            default -> throw new IllegalStateException("Unexpected value: " + inputParam);
        };
    }
}
