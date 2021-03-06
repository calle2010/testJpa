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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

/**
 * Spring configuration class
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "testJpa" })
@EnableJpaRepositories(basePackages = { "testJpa.spring" })
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
        final JpaTransactionManager txm = new JpaTransactionManager();
        txm.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        return txm;
    }

    /**
     * Simple instantiation of a Spring Entity Manager Factory. Depends on
     * "liquibase" bean to make sure it is created after LiquiBase has setup the
     * database schema.
     * 
     * @param dataSource
     *            the data source to use
     *
     * @return entity manager factory bean
     */
    @Bean
    @DependsOn("liquibase")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);

        // create EclipseLink vendor adapter
        EclipseLinkJpaVendorAdapter ejva = new EclipseLinkJpaVendorAdapter();
        // set lazy database transactions to true: transactions are only started
        // when commit is required. This enables EclipseLink's shared cache, but
        // has an impact on consistency for JDBC access to the same
        // connection/transaction.
        // Check EclipseLink logging level FINER too see when DB transactions
        // are started.
        // See SPR-7753
        ejva.getJpaDialect()
                .setLazyDatabaseTransaction(true);

        lcemfb.setJpaVendorAdapter(ejva);

        lcemfb.setDataSource(dataSource);
        return lcemfb;
    }

    /**
     * Setup Liquibase. By setDropFirst(true) Liquibase will drop the schema,
     * including sequences, on each invocation. This means that tests can use
     * DirtiesContext annotation to enforce Liquibase to clear the database if
     * required.
     * 
     * @param dataSource
     *            the data source to use
     *
     * @return the Liquibase bean
     */
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        final SpringLiquibase lqb = new SpringLiquibase();

        lqb.setDataSource(dataSource);
        lqb.setChangeLog("classpath:liquibase/db.changelog.xml");
        lqb.setDropFirst(true);

        final Map<String, String> params = new HashMap<>();
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
