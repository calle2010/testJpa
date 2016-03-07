package testJpa.spring.table.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.table.domain.SpringTable;

/**
 * Test CRUD functionality of a simple table without relationships.
 * <p>
 * Will not roll back after tests so that JPA executes the updates and DBUnit
 * picks up the changes.
 * <p>
 * All methods which update data need a @Transactional annotation together
 * with @DirtiesContext so that the entity manager etc. will be fresh for the
 * next test. This is required since DBUnit will update the test data but the
 * entity manager will still have outdated entries.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
public class SpringTableTest {

    @Autowired
    SpringTableDao dao;

    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringTableTest.class);

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testCount() {
        assertEquals(3, dao.count());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DatabaseSetup("setup_SpringTable.xml")
    @ExpectedDatabase(value = "expect_SpringTable_created.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testCreate() {
        final SpringTable st = new SpringTable();
        st.setData("new entry");

        final SpringTable stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        assertEquals(4, dao.count());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testExists() {
        assertTrue(dao.exists(10001000l));
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testExistsFailing() {
        assertFalse(dao.exists(999l));
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testFindAll() {
        final Iterable<SpringTable> allEntries = dao.findAll();
        final List<SpringTable> list = new ArrayList<>();

        for (final SpringTable st : allEntries) {
            list.add(st);
        }

        assertEquals(3, list.size());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testFindByData() {
        final List<SpringTable> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        assertEquals(10001000, list.get(0).getId().longValue());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testFindByDataFailing() {
        final Iterable<SpringTable> entities = dao.findByData("does not exist");

        final Iterator<SpringTable> ei = entities.iterator();

        assertFalse(ei.hasNext());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testFindById() {
        final SpringTable entity = dao.findOne(10001000l);
        assertEquals(10001000, entity.getId().longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testFindByIdFailing() {
        assertNull(dao.findOne(999l));
    }

    @Test
    @DatabaseSetup("setup_SpringTable_empty.xml")
    public void testIsEmpty() {
        assertTrue(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    public void testisNotEmpty() {
        assertFalse(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    @ExpectedDatabase(value = "expect_SpringTable_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void testRemoveManaged() {
        final SpringTable st = dao.findOne(10001000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(10001000l));
        assertEquals("must be one entry less", 2, dao.count());

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    @ExpectedDatabase(value = "expect_SpringTable_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void testUpdateManaged() {
        LOGGER.info("start test update managed");

        final SpringTable st = dao.findOne(10001000l);

        st.setData("updated");

        em.flush();

        LOGGER.info("end test update managed");
    }

    @Test
    @DatabaseSetup("setup_SpringTable.xml")
    @ExpectedDatabase(value = "expect_SpringTable_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void testUpdateUnmanaged() {
        LOGGER.info("start test update unmanaged");
        final SpringTable st = new SpringTable();
        st.setId(10001000l);
        st.setData("updated");

        dao.save(st);
        em.flush();
        LOGGER.info("end test update unmanaged");
    }

}
