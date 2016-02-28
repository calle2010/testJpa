package testJpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Configuration specific for repository tests. Imports the standard repository
 * configuration.
 */
@Configuration
@Import(TestJpaConfiguration.class)
@PropertySource("classpath:database.properties")
public class TestJpaTestConfiguration {

    @Autowired
    Environment env;

    /**
     * data source to be used
     *
     * @return the data source
     */
    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        final String url = env.getProperty("jdbc.url");
        ds.setUrl(url);
        if (!url.startsWith("jdbc:derby:memory")) {
            // set other JDBC properties only if not Derby In-Memory test
            ds.setUsername(env.getProperty("jdbc.username"));
            ds.setPassword(env.getProperty("jdbc.password"));
            ds.setDriverClassName(env.getProperty("jdbc.driver"));
        }
        return ds;
    }

}
