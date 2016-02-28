package testJpa.simpleTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import testJpa.TestJpaConfiguration;
import testJpa.simpleTable.dao.SimpleTableDao;
import testJpa.simpleTable.domain.SimpleTable;

/**
 * Test CRUD functionality of a simple table without relationships.
 * <p>
 * Will not roll back after tests so that JPA executes the updates and a new
 * TestTransaction can be used to verify the database contents.
 * <p>
 * All methods which update data need a @Transactional annotation together
 * with @DirtiesContext so that the entity manager etc. will be fresh for the
 * next test. This is required since DBUnit will update the test data but the
 * entity manager will still have outdated entries.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestJpaConfiguration.class })
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
@Rollback(false)
public class SimpleTableTestTransactionTest {

    @Autowired
    SimpleTableDao dao;

    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTableTestTransactionTest.class);

    @Test
    public void testCount() {

        setupSimpleTable();

        SimpleTable st = dao.findOne(1000l);
        assertEquals("one thousand", st.getData());

        LOGGER.info("after count");
    }

    /**
     * Flush and clear the entity manager to enforce database reads after the
     * update. End the transaction.
     */
    private void endTransactionAfterUpdate() {
        em.flush();
        em.clear();
        TestTransaction.end();
    }

    private void setupSimpleTable() {

        assertTrue("transaction must be active", TestTransaction.isActive());
        assertFalse("transaction must be set to commit", TestTransaction.isFlaggedForRollback());

        final SimpleTable st1 = new SimpleTable();
        st1.setId(1000l);
        st1.setData("one thousand");
        final SimpleTable st2 = new SimpleTable();
        st2.setId(1001l);
        st2.setData("one thousand one");
        final SimpleTable st3 = new SimpleTable();
        st3.setId(1002l);
        st3.setData("one thousand two");

        dao.save(st1);
        dao.save(st2);
        dao.save(st3);

        endTransactionAfterUpdate();

    }

    @Test
    public void testCreate() {
        setupSimpleTable();

        TestTransaction.start();

        final SimpleTable st = new SimpleTable();
        st.setData("new entry");

        final SimpleTable stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        endTransactionAfterUpdate();

        assertEquals(4, dao.count());

        assertEquals(1, dao.findByData("new entry").size());
    }

    @Test
    public void testExists() {
        setupSimpleTable();

        assertTrue(dao.exists(1000l));
    }

    @Test
    public void testExistsFailing() {
        setupSimpleTable();
        assertFalse(dao.exists(999l));
    }

    @Test
    public void testFindAll() {
        setupSimpleTable();
        final List<SimpleTable> list = dao.findAll();

        assertEquals(3, list.size());
    }

    @Test
    public void testFindByData() {
        setupSimpleTable();
        final List<SimpleTable> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        assertEquals(1000, list.get(0).getId().longValue());
    }

    @Test
    public void testFindByDataFailing() {
        setupSimpleTable();
        final List<SimpleTable> entities = dao.findByData("does not exist");

        assertTrue(CollectionUtils.isEmpty(entities));
    }

    @Test
    public void testFindById() {
        setupSimpleTable();
        final SimpleTable entity = dao.findOne(1000l);
        assertEquals(1000, entity.getId().longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    public void testFindByIdFailing() {
        setupSimpleTable();
        assertNull(dao.findOne(999l));
    }

    @Test
    public void testIsEmpty() {
        setupSimpleTable();
        TestTransaction.start();
        dao.deleteAllInBatch();
        endTransactionAfterUpdate();
        assertTrue(dao.isEmpty());
    }

    @Test
    public void testisNotEmpty() {
        setupSimpleTable();
        assertFalse(dao.isEmpty());
    }

    @Test
    public void testRemoveManaged() {
        setupSimpleTable();

        TestTransaction.start();

        final SimpleTable st = dao.findOne(1000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        endTransactionAfterUpdate();

        assertNull("most not find deleted entity", dao.findOne(1000l));
        assertEquals("must be one entry less", 2, dao.count());

    }

    @Test
    public void testUpdateManaged() {
        LOGGER.info("start test update managed");
        setupSimpleTable();

        TestTransaction.start();

        final SimpleTable st = dao.findOne(1000l);

        st.setData("updated");

        dao.save(st);

        endTransactionAfterUpdate();
        LOGGER.info("end test update managed");

        assertEquals("updated", dao.findOne(1000l).getData());
    }

    @Test
    public void testUpdateManagedWithoutMerge() {
        LOGGER.info("start test update managed");
        setupSimpleTable();

        TestTransaction.start();

        final SimpleTable st = dao.findOne(1000l);

        st.setData("updated");

        // no call to dao.save here!

        endTransactionAfterUpdate();
        LOGGER.info("end test update managed");

        assertEquals("updated", dao.findOne(1000l).getData());
    }

    @Test
    public void testUpdateUnmanaged() {
        LOGGER.info("start test update unmanaged");
        final SimpleTable st = new SimpleTable();
        st.setId(1000l);
        st.setData("updated");

        dao.save(st);
        endTransactionAfterUpdate();

        assertEquals("updated", dao.findOne(1000l).getData());
        LOGGER.info("end test update unmanaged");
    }

}