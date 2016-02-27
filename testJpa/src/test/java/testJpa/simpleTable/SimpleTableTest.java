package testJpa.simpleTable;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import testJpa.TestJpaConfiguration;
import testJpa.simpleTable.dao.SimpleTableDao;

/**
 * test CRUD functionality of a simple table without relationships
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaConfiguration.class)
public class SimpleTableTest {

    @Autowired
    SimpleTableDao dao;

    @Test
    public void testFindById() {
        fail("not yet implemented");
    }

    @Test(expected = DataAccessException.class)
    public void testFindByIdFailing() {
        fail("not yet implemented");
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
        fail("not yet implemented");
    }

    @Test
    public void testExists() {
        fail("not yet implemented");
    }

    @Test
    public void testExistsFailing() {
        fail("not yet implemented");
    }

    @Test
    public void testCreate() {
        fail("not yet implemented");
    }

    @Test
    public void testCreateAlreadyExists() {
        fail("not yet implemented");
    }

    @Test
    public void testUpdate() {
        fail("not yet implemented");
    }

    @Test
    public void testRemove() {
        fail("not yet implemented");
    }

    @Test(expected = DataAccessException.class)
    public void testRemoveFailing() {
        fail("not yet implemented");
    }

    @Test
    public void testCount() {
        fail("not yet implemented");
    }

    @Test
    public void testisEmpty() {
        fail("not yet implemented");
    }

    @Test
    public void testisEmptyFalse() {
        fail("not yet implemented");
    }

}
