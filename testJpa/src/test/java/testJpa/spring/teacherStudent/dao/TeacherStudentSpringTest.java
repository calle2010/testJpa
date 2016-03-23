package testJpa.spring.teacherStudent.dao;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

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

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.teacherStudent.domain.StudentSpring;
import testJpa.spring.teacherStudent.domain.TeacherSpring;

/**
 * Test CRUD functionality of the tables with many-to-many relationship.
 * <p>
 * This class uses DBUnit for database setup and verification of results. All
 * changes are rolled back at the end of a test method.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@Transactional
public class TeacherStudentSpringTest {

    @Autowired
    TeacherSpringDao teacherDao;

    @Autowired
    StudentSpringDao studentDao;

    @PersistenceContext
    EntityManager em;

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    public void testStudentsByTeacher() {
        List<TeacherSpring> teachers = teacherDao.findAll();

        assertThat(teachers,
                containsInAnyOrder(
                        // one teacher with one student
                        hasProperty("students", contains(hasProperty("data", is("two thousand")))),
                        // one teacher with two students
                        hasProperty("students",
                                containsInAnyOrder(
                                        // student 1
                                        hasProperty("data", is("two thousand")),
                                        // student 2
                                        hasProperty("data", is("two thousand one")))),
                        // one teacher without student
                        hasProperty("students", hasSize(0))));
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    public void testCountTeachersByStudents() {
        List<StudentSpring> students = studentDao.findAll();

        assertThat(students, hasSize(9));

        assertThat(students,
                hasItem(
                        // one student with one teacher
                        hasProperty("teachers", contains(hasProperty("data", is("one thousand one"))))));

        assertThat(students,
                hasItem(
                        // one student with two teachers
                        hasProperty("teachers",
                                containsInAnyOrder(
                                        // teacher 1
                                        hasProperty("data", is("one thousand")),
                                        // teacher 2
                                        hasProperty("data", is("one thousand one"))))));

        assertThat(students,
                hasItem(
                        // all other students without teachers
                        hasProperty("teachers", hasSize(0))));
    }
}
