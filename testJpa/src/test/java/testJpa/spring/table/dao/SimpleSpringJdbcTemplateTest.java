package testJpa.spring.table.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.table.domain.SpringTable;

/**
 * Test CRUD functionality of a simple table without relationships.
 * <p>
 * This class uses {@link JdbcTemplate} for database setup and verification of
 * results. All changes are rolled back at the end of a test method.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@Transactional
public class SimpleSpringJdbcTemplateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringJdbcTemplateTest.class);

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

    @Test
    public void testCount() {

        setupSimpleSpring();

        assertEquals(3, dao.count());
    }

    private void setupSimpleSpring() {

        assertTrue("transaction must be active", TestTransaction.isActive());
        assertTrue("transaction is set to rollback", TestTransaction.isFlaggedForRollback());

        jdbc.update("delete from SPRING_TABLE");

        jdbc.update("insert into SPRING_TABLE values(?, ?)", 10001000l, "one thousand");
        jdbc.update("insert into SPRING_TABLE values(?, ?)", 10001002l, "one thousand two");
        jdbc.update("insert into SPRING_TABLE values(?, ?)", 10001001l, "one thousand one");

    }

    @Test
    public void testCreate() {
        setupSimpleSpring();

        final SpringTable st = new SpringTable();
        st.setData("new entry");

        final SpringTable stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        // flush is required so that the EntityManager executes the SQL
        // statements for insert
        em.flush();

        assertEquals(Long.valueOf(4), jdbc.queryForObject("select count(ID) from SPRING_TABLE", Long.class));

        assertEquals(1, jdbc.queryForList("select id from SPRING_TABLE where data = 'new entry'").size());

    }

    @Test
    public void testExists() {
        setupSimpleSpring();

        assertTrue(dao.exists(10001000l));
    }

    @Test
    public void testExistsFailing() {
        setupSimpleSpring();
        assertFalse(dao.exists(999l));
    }

    @Test
    public void testFindAll() {
        setupSimpleSpring();
        final List<SpringTable> list = dao.findAll();

        assertEquals(3, list.size());
    }

    @Test
    public void testFindByData() {
        setupSimpleSpring();
        final List<SpringTable> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        assertEquals(10001000, list.get(0).getId().longValue());
    }

    @Test
    public void testFindByDataFailing() {
        setupSimpleSpring();
        final List<SpringTable> entities = dao.findByData("does not exist");

        assertTrue(CollectionUtils.isEmpty(entities));
    }

    @Test
    public void testFindById() {
        setupSimpleSpring();
        final SpringTable entity = dao.findOne(10001000l);
        assertEquals(10001000, entity.getId().longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    public void testFindByIdFailing() {
        setupSimpleSpring();
        assertNull(dao.findOne(999l));
    }

    @Test
    public void testIsEmpty() {
        jdbc.update("delete from SPRING_TABLE");
        em.clear();

        assertTrue(dao.isEmpty());
    }

    @Test
    public void testisNotEmpty() {
        setupSimpleSpring();
        assertFalse(dao.isEmpty());
    }

    @Test
    public void testRemoveManaged() {
        setupSimpleSpring();

        final SpringTable st = dao.findOne(10001000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        em.flush();

        assertTrue("most not find deleted entity",
                jdbc.queryForList("select id from SPRING_TABLE where id = 10001000").isEmpty());
        assertEquals("must be one entry less", Long.valueOf(2),
                jdbc.queryForObject("select count(ID) from SPRING_TABLE", Long.class));

    }

    @Test
    public void testUpdateManaged() {
        setupSimpleSpring();

        final SpringTable st = dao.findOne(10001000l);

        st.setData("updated");

        // The call to save is not needed here, but left in to show that it is
        // also ok. See also testUpdateManagedWithoutMerge()
        dao.saveAndFlush(st);

        assertEquals("updated", jdbc.queryForObject("select data from SPRING_TABLE where id = 10001000", String.class));
    }

    @Test
    public void testUpdateManagedWithoutMerge() {
        setupSimpleSpring();

        final SpringTable st = dao.findOne(10001000l);

        st.setData("updated");

        // no call to dao.save here!
        em.flush();

        assertEquals("updated", jdbc.queryForObject("select data from SPRING_TABLE where id = 10001000", String.class));
    }

    @Test
    public void testUpdateUnmanaged() {
        final SpringTable st = new SpringTable();
        st.setId(10001000l);
        st.setData("updated");

        dao.save(st);
        em.flush();

        assertEquals("updated", jdbc.queryForObject("select data from SPRING_TABLE where id = 10001000", String.class));
    }

}
