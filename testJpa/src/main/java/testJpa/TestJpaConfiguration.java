package testJpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
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
}
