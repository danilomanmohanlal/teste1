package pt.scml.fin.batch.core.config.db;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TaskConfiguration {

    @Value("${spring.cloud.task.table-prefix}")
    private String taskTablePrefix;

    @Bean
    public TaskConfigurer taskConfigurer(@Qualifier("finDataSource") DataSource dataSource) {
        return new DefaultTaskConfigurer(dataSource, taskTablePrefix, null);
    }
}