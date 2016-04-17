package alertmed.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Simple Java Spring configuration to be used for the Spring example application. This configuration is mainly
 * composed of a database configuration and initial population via the script "products.sql" of the database for
 * querying by our Spring evaluationAndMatchingService bean.
 * <p>
 * The Spring evaluationAndMatchingService bean and repository are scanned for via @EnableJpaRepositories and @ComponentScan annotations
 */
@Slf4j
@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySource("${spring.config.name}")
public class DatabaseConfig {

    @Autowired
    private Environment env;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.username"));
        dataSource.setPassword(env.getProperty("jdbc.password"));
        return dataSource;
    }

    @Bean
    @Autowired
    public Flyway flyway(DataSource dataSource) {
        log.info("Initializing database migrations");
        final Flyway flyway = new Flyway();
        Map<String, Object> map = extractMigrationProperties();
        Properties properties = new Properties();
        properties.putAll(map);
        flyway.configure(properties);
        flyway.setDataSource(dataSource);
        flyway.migrate();

        return flyway;
    }

    private Map<String, Object> extractMigrationProperties() {
        Map<String, Object> map = new HashMap();
        for (Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            org.springframework.core.env.PropertySource propertySource = (org.springframework.core.env.PropertySource) it.next();
            if (propertySource instanceof MapPropertySource) {
                map.putAll(((MapPropertySource) propertySource).getSource());
            }

        }

        return map;
    }

    @Bean
    @Autowired
    @DependsOn(value = "flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("alertmed");
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto", "validate"));
        jpaProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", "true"));
        jpaProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect"));

        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean
    @Autowired
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
