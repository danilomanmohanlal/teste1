package pt.scml.fin.job.ad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pt.scml.fin.model.config.db.FinDatasourceConfig;

@SpringBootApplication
@Import({FinDatasourceConfig.class})
public class JobAdApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobAdApplication.class, args);
    }
}
