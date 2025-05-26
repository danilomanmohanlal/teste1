package pt.scml.fin.model.config.db;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "pt.scml.fin.model.repo",
    entityManagerFactoryRef = "finEntityManagerFactory",
    transactionManagerRef = "finTransactionManager")
public class FinDatasourceConfig {

    @Value("${spring.batch.jdbc.table-prefix}")
    private String tablePrefix;

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource") //inherit properties from SCDF
    public DataSourceProperties finDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "finDataSource")
    @ConfigurationProperties("spring.datasource.configuration") //inherit properties from SCDF
    public DataSource finDataSource() {
        return finDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
    }

    @Primary
    @Bean(name = "finEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean finEntityManagerFactory(
        EntityManagerFactoryBuilder builder) {
        return builder
            .dataSource(finDataSource())
            .packages("pt.scml.fin.model")
            .persistenceUnit("fin")
            .properties(hibernateProperties())
            .build();
    }

    @Primary
    @Bean(name = "finTransactionManager")
    public PlatformTransactionManager finTransactionManager(
        @Qualifier("finDataSource") DataSource dataSource) {
        return new JpaTransactionManager();
    }

    @Bean(name = "finJobRepository")
    public JobRepository jobRepository(@Qualifier("finDataSource") DataSource dataSource,
        @Qualifier("finTransactionManager") PlatformTransactionManager transactionManager)
        throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setTablePrefix(tablePrefix);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        return properties;
    }
}

