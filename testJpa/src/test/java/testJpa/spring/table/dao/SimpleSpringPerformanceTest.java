package testJpa.spring.table.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.table.domain.SpringTable;

/**
 * Create/read/update a high number of records to test performance. Changes are
 * committed to take this time into account.
 * 
 * Switch EclipseLink logging to SEVERE and other loggers to INFO to prevent
 * slow performance due to logging.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@Transactional
@Commit
public class SimpleSpringPerformanceTest {

    private static final int SO_MANY = 100000;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringPerformanceTest.class);

    StopWatch sw = new StopWatch();

    @Autowired
    SpringTableDao dao;

    @PersistenceContext
    EntityManager em;

    private JdbcTemplate jdbc;

    /**
     * @param dataSource
     *            the data source to inject to the JDBC Template
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @After
    public void tearDown() {
        LOGGER.info(sw.prettyPrint());
    }

    /**
     * Create a high number of records.
     * <p>
     * Specifies @DirtiesContext so that every repetition starts fresh.
     * <p>
     * With batch writing disabled (none):
     * 
     * <pre>
     * 2.000 times: VALUES(NEXT VALUE FOR SEQ_SIMPLE_TABLE_ID)
     * 100.000 times: INSERT INTO SIMPLE_TABLE (ID, DATA) VALUES (?, ?)
     * </pre>
     * <p>
     * With batch writing enabled (JDBC):
     * 
     * <pre>
     * 2.000 times: VALUES(NEXT VALUE FOR SEQ_SIMPLE_TABLE_ID)
     * 1.000 times: [EL Fine]INSERT INTO SIMPLE_TABLE (ID, DATA) VALUES (?, ?)
     * 100.0000:    [EL Fine] bind => [86689, data]
     * </pre>
     * 
     * Results (average of executions 3-10, after warm-up, log levels
     * SEVERE/INFO):
     * 
     * <pre>
     * batch-writing JDBC, SimpleTable: 4.689ms (100%)
     * batch-writing JDBC, SpringTable: 5.093ms (109%)
     * batch-writing none, SimpleTable: 7.778ms (166%)
     * batch-writing none, SpringTable: 8.287ms (177%)
     * </pre>
     * 
     * It seems like SpringTable is slightly slower due to the
     * automatic @Transactional configuration.
     */
    @Test
    @Repeat(10)
    @DirtiesContext
    public void testCreateManyRecords() {

        TestTransaction.flagForCommit();
        sw.start("create");
        createRecords(SO_MANY);
        TestTransaction.end();
        sw.stop();
    }

    @Test
    @Repeat(5)
    @DirtiesContext
    public void testCreateReadManyRecords() {

        sw.start("create");
        dao.deleteAll();
        createRecords(SO_MANY);
        TestTransaction.end();
        sw.stop();

        sw.start("read");
        List<SpringTable> result = dao.findAll();
        sw.stop();
        assertEquals(SO_MANY, result.size());
    }

    @Test
    @Repeat(5)
    @DirtiesContext
    public void testCreateUpdateManyRecords() {

        sw.start("create");
        List<SpringTable> stList = createRecords(SO_MANY);
        TestTransaction.end();
        sw.stop();

        sw.start("update");
        TestTransaction.start();
        // findOne() will not result in a SELECT since it can be fulfilled from
        // L2 cache.
        // L2 cache can be evicted if required:
        // em.getEntityManagerFactory().getCache().evict(SpringTable.class);
        for (int i = 0; i < SO_MANY; i += 100) {
            SpringTable st = dao.findOne(stList.get(i).getId());
            st.setData("data updated");
        }
        TestTransaction.end();
        sw.stop();
    }

    private List<SpringTable> createRecords(int totalRecords) {

        List<SpringTable> stList = new ArrayList<>(totalRecords);

        for (int i = 0; i < totalRecords; i++) {
            SpringTable st = new SpringTable();
            st.setData("data");
            stList.add(dao.save(st));
        }

        // trigger writing
        em.flush();
        return stList;

    }

}
