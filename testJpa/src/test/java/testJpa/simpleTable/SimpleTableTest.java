package testJpa.simpleTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
import testJpa.simpleTable.dao.SimpleTableDao;
import testJpa.simpleTable.domain.SimpleTable;

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
public class SimpleTableTest {

    @Autowired
    SimpleTableDao dao;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTableTest.class);

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testCount() {
        assertEquals(3, dao.count());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DatabaseSetup("setup_SimpleTable.xml")
    @ExpectedDatabase(value = "expect_SimpleTable_created.xml", table = "SIMPLE_TABLE", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @DirtiesContext
    public void testCreate() {
        SimpleTable st = new SimpleTable();
        st.setData("new entry");

        SimpleTable stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        assertEquals(4, dao.count());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testExists() {
        assertTrue(dao.exists(1000l));
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testExistsFailing() {
        assertFalse(dao.exists(999l));
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindAll() {
        List<SimpleTable> list = dao.findAll();

        assertEquals(3, list.size());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindByData() {
        List<SimpleTable> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        assertEquals(1000, list.get(0).getId().longValue());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindByDataFailing() {
        List<SimpleTable> entities = dao.findByData("does not exist");

        assertTrue(CollectionUtils.isEmpty(entities));
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindById() {
        SimpleTable entity = dao.findOne(1000l);
        assertEquals(1000, entity.getId().longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindByIdFailing() {
        assertNull(dao.findOne(999l));
    }

    @Test
    @DatabaseSetup("setup_SimpleTable_empty.xml")
    public void testIsEmpty() {
        assertTrue(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testisNotEmpty() {
        assertFalse(dao.isEmpty());
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    @ExpectedDatabase(value = "expect_SimpleTable_deleted.xml", table = "SIMPLE_TABLE", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testRemoveManaged() {
        SimpleTable st = dao.findOne(1000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(1000l));
        assertEquals("must be one entry less", 2, dao.count());

    }

    /**
     * Test how JPA behaves in this case. @Transactional and SpringDBUnit
     * annotations will change the expected exception result:
     * InvalidDataAccessApiUsageException.
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testRemoveUnmanaged() {
        SimpleTable st = new SimpleTable();
        st.setId(1000l);

        // this must fail since the entity to delete is unmanaged
        dao.delete(st);

    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    @ExpectedDatabase(value = "expect_SimpleTable_updated.xml", table = "SIMPLE_TABLE", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateManaged() {
        LOGGER.info("start test update managed");

        SimpleTable st = dao.findOne(1000l);

        st.setData("updated");

        dao.save(st);
        LOGGER.info("end test update managed");
    }

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    @ExpectedDatabase(value = "expect_SimpleTable_updated.xml", table = "SIMPLE_TABLE", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateUnmanaged() {
        LOGGER.info("start test update unmanaged");
        SimpleTable st = new SimpleTable();
        st.setId(1000l);
        st.setData("updated");

        dao.save(st);
        LOGGER.info("end test update unmanaged");
    }

}
