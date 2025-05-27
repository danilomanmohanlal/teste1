package pt.scml.fin.job.li_itms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pt.scml.fin.model.config.db.FinDatasourceConfig;

@SpringBootApplication
@Import({FinDatasourceConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
