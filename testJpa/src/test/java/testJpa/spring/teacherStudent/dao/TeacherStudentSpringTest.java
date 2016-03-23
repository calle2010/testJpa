package testJpa.spring.teacherStudent.dao;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

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
                describedAs("shall contain three teachers",
                        containsInAnyOrder(
                                describedAs("one teacher with one student",
                                        hasProperty("students", contains(hasProperty("data", is("two thousand"))))),
                                describedAs("one teacher with two students",
                                        hasProperty("students",
                                                containsInAnyOrder(
                                                        // student 1
                                                        hasProperty("data", is("two thousand")),
                                                        // student 2
                                                        hasProperty("data", is("two thousand one"))))),
                                describedAs("one teacher without student", hasProperty("students", hasSize(0))))));
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    public void testTeachersByStudents() {
        List<StudentSpring> students = studentDao.findAll();

        assertThat(students, hasSize(9));

        assertThat(students, describedAs("one student with one teacher",
                hasItem(hasProperty("teachers", contains(hasProperty("data", is("one thousand one")))))));

        assertThat(students,
                describedAs("one student with two teachers",
                        hasItem(hasProperty("teachers",
                                containsInAnyOrder(
                                        // teacher 1
                                        hasProperty("data", is("one thousand")),
                                        // teacher 2
                                        hasProperty("data", is("one thousand one")))))));

        assertThat(students,
                describedAs("all other students have no teachers", hasItem(hasProperty("teachers", hasSize(0)))));
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "setup_TeacherSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_StudentSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemoveStudentFromTeacher() {
        // get a teacher with one student
        TeacherSpring teacher = teacherDao.findOne(10001000l);

        List<StudentSpring> students = teacher.getStudents();

        assertThat(students, hasSize(1));
        assertThat("teacher shall have relation to student", students.get(0), hasProperty("id", is(20001000l)));

        // get the student through the DAO: it is the same instance and has the
        // inverse relationship
        StudentSpring student = studentDao.findOne(students.get(0).getId());
        assertThat(student, sameInstance(students.get(0)));
        assertThat("student shall have relation to teacher", student.getTeachers(),
                hasItem(hasProperty("id", is(teacher.getId()))));

        // remove the student from the teacher
        assertTrue(teacher.removeStudent(student));

        // ensure the teacher's got no students
        assertTrue(teacher.getStudents().isEmpty());

        // ensure the teacher is removed from the student
        assertThat("student shall have no relation to teacher", student.getTeachers(),
                not(hasItem(hasProperty("id", is(teacher.getId())))));

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "setup_TeacherSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_StudentSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemoveTeacherFromStudent() {
        // get a student with one teacher
        StudentSpring student = studentDao.findOne(20001000l);

        List<TeacherSpring> teachers = student.getTeachers();

        int numOfTeachers = teachers.size();
        assertThat("teacher shall have relation to student", teachers.get(0), hasProperty("id", is(10001000l)));

        // get the teacher through the DAO: it is the same instance and has the
        // inverse relationship
        TeacherSpring teacher = teacherDao.findOne(teachers.get(0).getId());
        assertThat(teacher, sameInstance(teachers.get(0)));
        assertThat("teacher shall have relation to student", teacher.getStudents(),
                hasItem(hasProperty("id", is(student.getId()))));

        // remove the student from the teacher
        assertTrue(student.removeTeacher(teacher));

        // ensure the student's got one teacher less
        assertThat(student.getTeachers(), hasSize(numOfTeachers - 1));

        // ensure the student is removed from the teacher
        assertThat("teacher shall have no relation to student", teacher.getStudents(),
                not(hasItem(hasProperty("id", is(student.getId())))));

        em.flush();
    }
}