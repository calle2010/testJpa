package testJpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring configuration class
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = { "testJpa" })
public class TestJpaConfiguration {

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
     * Simple instantiation of a Spring Entity Manager Factory
     * 
     * @return entity manager factory bean
     */
    @Bean
    LocalContainerEntityManagerFactoryBean localEntityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPersistenceUnitName("testJpa");
        lcemfb.setJpaDialect(new EclipseLinkJpaDialect());
        return lcemfb;
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
