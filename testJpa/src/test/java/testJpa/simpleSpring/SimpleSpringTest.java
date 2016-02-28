package testJpa.simpleSpring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import testJpa.TestJpaConfiguration;
import testJpa.simpleSpring.dao.SimpleSpringDao;
import testJpa.simpleSpring.domain.SimpleSpring;

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
@ContextConfiguration(classes = { TestJpaConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class })
@Rollback(false)
public class SimpleSpringTest {

    @Autowired
    SimpleSpringDao dao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSpringTest.class);

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testCount() {
        assertEquals(3, dao.count());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DatabaseSetup("setup_SimpleSpring.xml")
    @ExpectedDatabase(value = "expect_SimpleSpring_created.xml", table = "SIMPLE_SPRING", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @DirtiesContext
    public void testCreate() {
        final SimpleSpring st = new SimpleSpring();
        st.setData("new entry");

        final SimpleSpring stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        assertEquals(4, dao.count());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testExists() {
        assertTrue(dao.exists(1000l));
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testExistsFailing() {
        assertFalse(dao.exists(999l));
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testFindAll() {
        final Iterable<SimpleSpring> allEntries = dao.findAll();
        final List<SimpleSpring> list = new ArrayList<>();

        for (final SimpleSpring st : allEntries) {
            list.add(st);
        }

        assertEquals(3, list.size());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testFindByData() {
        final Iterable<SimpleSpring> entities = dao.findByData("one thousand");

        final Iterator<SimpleSpring> ei = entities.iterator();

        final SimpleSpring entity = ei.next();

        assertEquals(1000, entity.getId().longValue());
        assertFalse(ei.hasNext());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testFindByDataFailing() {
        final Iterable<SimpleSpring> entities = dao.findByData("does not exist");

        final Iterator<SimpleSpring> ei = entities.iterator();

        assertFalse(ei.hasNext());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testFindById() {
        final SimpleSpring entity = dao.findOne(1000l);
        assertEquals(1000, entity.getId().longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testFindByIdFailing() {
        assertNull(dao.findOne(999l));
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring_empty.xml")
    public void testIsEmpty() {
        assertTrue(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    public void testisNotEmpty() {
        assertFalse(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    @ExpectedDatabase(value = "expect_SimpleSpring_deleted.xml", table = "SIMPLE_SPRING", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testRemoveManaged() {
        final SimpleSpring st = dao.findOne(1000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(1000l));
        assertEquals("must be one entry less", 2, dao.count());

    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    @ExpectedDatabase(value = "expect_SimpleSpring_updated.xml", table = "SIMPLE_SPRING", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateManaged() {
        LOGGER.info("start test update managed");

        final SimpleSpring st = dao.findOne(1000l);

        st.setData("updated");

        dao.save(st);
        LOGGER.info("end test update managed");
    }

    @Test
    @DatabaseSetup("setup_SimpleSpring.xml")
    @ExpectedDatabase(value = "expect_SimpleSpring_updated.xml", table = "SIMPLE_SPRING", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateUnmanaged() {
        LOGGER.info("start test update unmanaged");
        final SimpleSpring st = new SimpleSpring();
        st.setId(1000l);
        st.setData("updated");

        dao.save(st);
        LOGGER.info("end test update unmanaged");
    }

}
