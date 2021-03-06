package testJpa.spring.teacherStudent.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.teacherStudent.domain.TeacherSpring;

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
public class TeacherSpringTest {

    @Autowired
    TeacherSpringDao dao;

    @PersistenceContext
    EntityManager em;

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    public void testCount() {
        assertEquals(3, dao.count());
    }

    /**
     * Uses @DirtiesContext because a database row is created and the sequence
     * change can't be rolled back. By dirtying the context Liquibase will on
     * next invocation drop the database and re-create.
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_created.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @DirtiesContext
    public void testCreate() {
        final TeacherSpring st = new TeacherSpring();
        st.setData("new entry");

        final TeacherSpring stPersisted = dao.save(st);

        assertNotEquals(0, stPersisted.getId()
                .longValue());

        assertEquals(4, dao.count());
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    public void testExists() {
        assertTrue(dao.exists(10001000l));
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    public void testFindAll() {
        assertEquals(3, dao.findAll()
                .size());
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    public void testFindById() {
        final TeacherSpring entity = dao.findOne(10001000l);
        assertEquals(10001000, entity.getId()
                .longValue());
        assertEquals("one thousand", entity.getData());
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemove() {
        final TeacherSpring st = dao.findOne(10001000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(10001000l));
        assertEquals("must be one entry less", 2, dao.count());

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testUpdate() {

        final TeacherSpring st = dao.findOne(10001000l);

        st.setData("updated");

        em.flush();

    }

}
