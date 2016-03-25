package testJpa.spring.teacherStudent.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A teacher table with many-to-many relationship to students. The teacher table
 * is defined as "owning" the relationship, even though there should be also
 * students with no teachers assigned.
 */
@Entity
@Table(name = "TEACHER_SPRING")
public class TeacherSpring {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TEACHER_SPRING_ID")
    @SequenceGenerator(name = "SEQ_TEACHER_SPRING_ID", sequenceName = "SEQ_TEACHER_SPRING_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

    private String data;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH })
    @JoinTable(name = "TEACHERS_STUDENTS_SPRING", joinColumns = @JoinColumn(name = "teacher_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "student_ID", referencedColumnName = "ID"))
    private List<StudentSpring> students = new ArrayList<>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * @return the students
     */
    public List<StudentSpring> getStudents() {
        return Collections.unmodifiableList(students);
    }

    /**
     * Remove student from teacher. Takes care of the bidirectional relationship
     * as well.
     * 
     * @param student
     *            the student to remove
     * @return true if student was contained in the relationship
     */
    public boolean removeStudent(StudentSpring student) {
        student.removeTeacherInternal(this);
        return removeStudentInternal(student);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Remove the student from this teacher. This is package private since it
     * doesn't take care of the inverse relationship.
     * 
     * @param student
     *            the student to remove
     * @return true if student was contained in the relationship
     */
    boolean removeStudentInternal(StudentSpring student) {
        return students.remove(student);
    }

    /**
     * add a student
     * 
     * @param student
     *            the student to add
     */
    public void addStudent(StudentSpring student) {
        student.addTeacherInternal(this);
        addStudentInternal(student);
    }

    /**
     * Add teacher to student. This is package private since it doesn't take
     * care of the inverse relationship.
     * 
     * @param student
     *            the student to add
     */

    void addStudentInternal(StudentSpring student) {
        students.add(student);

    }
}
