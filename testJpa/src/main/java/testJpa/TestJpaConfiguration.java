package testJpa;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

/**
 * Spring configuration class
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "testJpa" })
@EnableJpaRepositories(basePackages = { "testJpa.simpleSpring" })
public class TestJpaConfiguration {

    private static final String PERSISTENCE_UNIT_NAME = "testJpa";

    /**
     * Post processor to translate technology specific exceptions to Spring
     * {@link DataAccessException}
     * 
     * @return persistence exception translation post processor"
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * instantiate JPA transaction manager
     * 
     * @return the transaction manager
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager txm = new JpaTransactionManager();
        txm.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        return txm;
    }

    /**
     * Simple instantiation of a Spring Entity Manager Factory
     * 
     * @return entity manager factory bean
     */
    @Bean
    @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        lcemfb.setJpaDialect(new EclipseLinkJpaDialect());
        lcemfb.setDataSource(dataSource());
        return lcemfb;
    }

    /**
     * Instantiate the entity manager factory. This is required for
     * {@link JpaRepository}.
     * 
     * @return the entity manager factory
     */
    // @Bean
    // public EntityManagerFactory entityManagerFactory() {
    // LocalContainerEntityManagerFactoryBean lcemfb =
    // localEntityManagerFactoryBean();
    // return lcemfb.getNativeEntityManagerFactory();
    // }

    /**
     * data source to be used
     * 
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:derby:memory:test-jpa;create=true");
        return ds;
    }

    /**
     * setup Liquibase
     * 
     * @return the Liquibase bean
     */
    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase lqb = new SpringLiquibase();

        lqb.setDataSource(dataSource());
        lqb.setChangeLog("classpath:liquibase/db.changelog.xml");

        Map<String, String> params = new HashMap<>();
        params.put("verbose", "true");
        lqb.setChangeLogParameters(params);

        return lqb;

    }

    /**
     * enables @PersistenceUnit and @PersistenceContext annotations
     * 
     * @return persistence annotation post processor
     */
    @Bean
    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        return new PersistenceAnnotationBeanPostProcessor();
    }

}
