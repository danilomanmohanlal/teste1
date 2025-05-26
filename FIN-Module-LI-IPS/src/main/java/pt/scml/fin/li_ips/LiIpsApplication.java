package pt.scml.fin.li_ips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pt.scml.fin.model.config.db.FinDatasourceConfig;

@SpringBootApplication
@Import({FinDatasourceConfig.class})
public class LiIpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiIpsApplication.class, args);
    }
}
