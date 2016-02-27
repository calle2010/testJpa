package testJpa.simpleTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import testJpa.TestJpaConfiguration;
import testJpa.simpleTable.dao.SimpleTableDao;
import testJpa.simpleTable.domain.SimpleTable;

/**
 * test CRUD functionality of a simple table without relationships
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestJpaConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SimpleTableTest {

    @Autowired
    SimpleTableDao dao;

    @Test
    @DatabaseSetup("setup_SimpleTable.xml")
    public void testFindById() {
        SimpleTable entity = dao.findOne(1000);
        assertEquals(1000, entity.getId());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    public void testFindByIdFailing() {
        assertNull(dao.findOne(999));
    }

    @Test
    public void testFindByData() {
        fail("not yet implemented");
    }

    @Test(expected = DataAccessException.class)
    public void testFindByDataFailing() {
        fail("not yet implemented");
    }

    @Test
    public void testFindAll() {
        Iterable<SimpleTable> allEntries = dao.findAll();
        List<SimpleTable> list = new ArrayList<>();

        for (SimpleTable st : allEntries) {
            list.add(st);
        }

        assertEquals(3, list.size());
        assertEquals(1000, list.get(0).getId());
        assertEquals(1001, list.get(0).getId());
        assertEquals(1002, list.get(0).getId());
    }

    @Test
    public void testExists() {
        assertTrue(dao.exists(1000));
    }

    @Test
    public void testExistsFailing() {
        assertFalse(dao.exists(999));
    }

    @Test
    public void testCreate() {
        SimpleTable st = new SimpleTable();
        st.setData("new entry");

        SimpleTable stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId());

        assertEquals(4, dao.count().longValue());
    }

    @Test
    public void testCreateAlreadyExists() {
        SimpleTable st = new SimpleTable();
        st.setId(1000);
        st.setData("entry with duplicate key");

        dao.save(st);
    }

    @Test
    public void testUpdate() {
        SimpleTable st = dao.findOne(1000);

        st.setData("new data");

        SimpleTable stPersisted = dao.save(st);

        assertEquals("new data", st.getData());
    }

    @Test
    public void testRemoveManaged() {
        SimpleTable st = dao.findOne(1000);
        assertNotNull(st);

        dao.delete(st);

        assertNull(dao.findOne(1000));

    }

    @Test
    public void testRemoveUnmanaged() {
        SimpleTable st = new SimpleTable();
        st.setId(1000);

        dao.delete(st);

        assertNull(dao.findOne(1000));

    }

    @Test(expected = DataAccessException.class)
    public void testRemoveFailing() {
        SimpleTable st = new SimpleTable();
        st.setId(999);
        dao.delete(st);
    }

    @Test
    public void testCount() {
        assertEquals(3, dao.count().longValue());
    }

    @Test
    public void testisEmpty() {
        assertTrue(dao.isEmpty());
    }

    @Test
    public void testisEmptyFalse() {
        assertFalse(dao.isEmpty());
    }

}
