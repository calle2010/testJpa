package testJpa.spring.teacherStudent.dao;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TestTransaction;
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
 * 
 * The fix method order is required because of sequence generation which can't
 * be rolled back.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class })
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TeacherStudentSpringTest {

    @Autowired
    TeacherSpringDao teacherDao;

    @Autowired
    StudentSpringDao studentDao;

    @PersistenceContext
    EntityManager em;

    private JdbcTemplate jdbc;

    /**
     * @param dataSource
     *            the data source to inject to the JDBC Template
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

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
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted_teacher.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
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
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted_teacher.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
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

    /**
     * Remove a teacher. This should remove the relationship to students as
     * well. At the same time the students should be unaffected. This test will
     * fail if cascade=REMOVE is specified for the teacher entity.
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_deleted.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_StudentSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted_teacher.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemoveTeacher() {
        TeacherSpring teacher = teacherDao.findOne(10001000l);

        teacherDao.delete(teacher);

        em.flush();
    }

    /**
     * Remove a student. This should remove the relationship to teachers as
     * well. At the same time the teachers should be unaffected. This test will
     * fail if cascade=REMOVE is specified for the student entity.
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "setup_TeacherSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_StudentSpring_deleted.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_deleted_student.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testRemoveStudent() {
        StudentSpring student = studentDao.findOne(20001000l);

        studentDao.delete(student);

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "setup_TeacherSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_StudentSpring_created.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_created_student.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void test01CreateStudentAndRelateToTeacher() {
        StudentSpring student = new StudentSpring();
        student.setData("new entry");

        TeacherSpring teacher = teacherDao.findOne(10001000l);

        teacher.addStudent(student);

        assertThat("teacher shall have relation to student", teacher.getStudents(), hasItem(student));

        assertThat("student shall have relation to teacher", student.getTeachers(), hasItem(teacher));

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_created.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_StudentSpring.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_created_teacher.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void test02CreateTeacherAndRelateToStudent() {
        TeacherSpring teacher = new TeacherSpring();
        teacher.setData("new entry");

        StudentSpring student = studentDao.findOne(20001000l);

        student.addTeacher(teacher);

        assertThat("student shall have relation to teacher", student.getTeachers(), hasItem(teacher));

        assertThat("teacher shall have relation to student", teacher.getStudents(), hasItem(student));

        em.flush();
    }

    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_created.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_StudentSpring_created.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_TeacherStudent_created_both.xml", override = false, assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void test03CreateTeacherAndStudentWithRelation() {
        TeacherSpring teacher = new TeacherSpring();
        teacher.setData("new entry");

        StudentSpring student = new StudentSpring();
        student.setData("new entry");

        // it doesn't matter if teacher is added to student or vice versa
        // since the DAO methods take care of the inverse relationship
        // teacher.addStudent(student);
        student.addTeacher(teacher);

        // it also doesn't matter if student or teacher is saved since the
        // PERSIST operation is cascaded
        teacherDao.save(teacher);
        // studentDao.save(student);

        assertThat("student shall have relation to teacher", student.getTeachers(), hasItem(teacher));

        assertThat("teacher shall have relation to student", teacher.getStudents(), hasItem(student));

        em.flush();
    }

    /**
     * test the detach operation cascades from teacher to student
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    public void testCascadeDetachTeacher() {
        TeacherSpring teacher = teacherDao.findOne(10001001l);
        List<StudentSpring> students = teacher.getStudents();

        // assert that all entities are attached
        assertThat(em.contains(teacher), is(true));

        /*
         * It seems like EclipseLink is only cascading the detach operation for
         * loaded objects. Since the teachers collection is lazy-loaded, this
         * loop makes sure it is loaded.
         */
        for (StudentSpring student : students) {
            assertThat(em.contains(student), is(true));
        }

        // detach the teacher
        em.detach(teacher);

        // assert that all entities are detached
        assertThat(em.contains(teacher), is(false));

        for (StudentSpring student : students) {
            assertThat(em.contains(student), is(false));
        }

    }

    /**
     * Test the detach operation cascades from student to teacher to student.
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    public void testCascadeDetachStudent() {
        TeacherSpring teacher = teacherDao.findOne(10001001l);
        List<StudentSpring> students = teacher.getStudents();

        // assert that there are at least 2 students
        assertThat(students, hasSize(greaterThan(1)));

        // assert that all entities are attached
        assertThat(em.contains(teacher), is(true));

        for (StudentSpring student : students) {
            assertThat(em.contains(student), is(true));
        }

        // get one of the students, doesn't matter which
        StudentSpring student = students.get(0);

        /*
         * It seems like EclipseLink is only cascading the detach operation for
         * loaded objects. Since the teachers collection is lazy-loaded, the
         * call to size() makes sure it is loaded.
         */
        assertThat(student.getTeachers().size(), is(greaterThan(0)));

        // detach one of the students
        em.detach(student);

        // assert that all entities are detached
        assertThat(em.contains(teacher), is(false));

        for (StudentSpring st : students) {
            assertThat(em.contains(st), is(false));
        }

    }

    /**
     * test the merge operation cascades from teacher to student
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @ExpectedDatabase(value = "expect_TeacherSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_StudentSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void testCascadeMergeTeacherStudent() {
        TeacherSpring teacher = teacherDao.findOne(10001000l);
        StudentSpring student = teacher.getStudents().get(0);

        // For the cascade to work from student to teacher it is required that
        // the teacher collection is loaded.
        assertThat(student.getTeachers(), hasSize(greaterThan(0)));

        em.detach(teacher);
        em.detach(student);

        teacher.setData("updated");
        student.setData("updated");

        /*
         * Now merge the detached and updated records. Without cascade=MERGE
         * both save methods have to be called. With cascade=MERGE only one of
         * save(teacher) OR save(student) is required.
         */
        teacherDao.save(teacher);
        // studentDao.save(student);

        em.flush();

    }

    /**
     * Test the merge operation cascades from teacher to student. The merge is
     * required in this test because the write happens in a different
     * transaction than the read.
     * <p>
     * This test changes the database since it is required that changes are not
     * rolled back after end of the transaction. Therefore it also specifies
     * DirtiesContext so that changes in the L2 cache after commit are not
     * affecting following tests.
     */
    @Test
    @DatabaseSetup("setup_TeacherSpring.xml")
    @DatabaseSetup("setup_StudentSpring.xml")
    @DatabaseSetup("setup_TeacherStudent.xml")
    @Commit
    @DirtiesContext
    public void testCascadeMergeTeacherStudentInTransaction() {
        // End the transaction. Test fixture was created by DBUnit before.
        TestTransaction.end();

        TeacherSpring teacher = teacherDao.findOne(10001000l);
        StudentSpring student = teacher.getStudents().get(0);

        // For the cascade to work from student to teacher it is required that
        // the teacher collection is loaded.
        assertThat(student.getTeachers(), hasSize(greaterThan(0)));

        teacher.setData("updated");
        student.setData("updated");

        /*
         * Now merge the detached and updated records. Without cascade=MERGE
         * both save methods have to be called. With cascade=MERGE only one of
         * save(teacher) OR save(student) is required.
         */
        TestTransaction.start();
        assertFalse(TestTransaction.isFlaggedForRollback());
        // teacherDao.save(teacher);
        studentDao.save(student);

        // End the transaction to commit changes to the database
        TestTransaction.end();

        // Assert changes through JDBC to avoid reading through EclipseLinks L2
        // cache.
        assertEquals("updated",
                jdbc.queryForObject("select data from TEACHER_SPRING where id = 10001000", String.class));
        assertEquals("updated",
                jdbc.queryForObject("select data from STUDENT_SPRING where id = 20001000", String.class));

    }
}