package testJpa.spring.teacherStudent.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.teacherStudent.domain.StudentSpring;

/**
 * Test CRUD functionality of the table with many-to-many relationship but
 * ignore the relationship here.
 * <p>
 * This class uses DBUnit for database setup and verification of results. All
 * changes are rolled back at the end of a test method.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@Transactional
public class StudentSpringTest {

    @Autowired
    StudentSpringDao dao;

    @PersistenceContext
    EntityManager em;

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    public void testCount() {
        assertEquals(9, dao.count());
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    @ExpectedDatabase(value = "expect_StudentSpring_created.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testCreate() {
        final StudentSpring st = new StudentSpring();
        st.setData("new entry");

        final StudentSpring stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId().longValue());

        assertEquals(10, dao.count());
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    public void testExists() {
        assertTrue(dao.exists(20001000l));
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    public void testFindAll() {
        assertEquals(9, dao.findAll().size());
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    public void testFindById() {
        final StudentSpring entity = dao.findOne(20001000l);
        assertEquals(20001000, entity.getId().longValue());
        assertEquals("two thousand", entity.getData());
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    @ExpectedDatabase(value = "expect_StudentSpring_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemove() {
        final StudentSpring st = dao.findOne(20001000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(20001000l));
        assertEquals("must be one entry less", 8, dao.count());

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_StudentSpring.xml")
    @ExpectedDatabase(value = "expect_StudentSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testUpdate() {

        final StudentSpring st = dao.findOne(20001000l);

        st.setData("updated");

        em.flush();

    }

}
