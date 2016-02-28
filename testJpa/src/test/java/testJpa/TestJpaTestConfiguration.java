package testJpa;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Configuration specific for repository tests. Imports the standard repository
 * configuration.
 */
@Configuration
@Import(TestJpaConfiguration.class)
public class TestJpaTestConfiguration {

    /**
     * data source to be used
     *
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl("jdbc:derby:memory:test-jpa;create=true");
        return ds;
    }

}
