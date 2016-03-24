package testJpa.spring.teacherStudent.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A student table with many-to-many relationship to teachers.
 */
@Entity
@Table(name = "STUDENT_SPRING")
public class StudentSpring {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_STUDENT_SPRING_ID")
    @SequenceGenerator(name = "SEQ_STUDENT_SPRING_ID", sequenceName = "SEQ_STUDENT_SPRING_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

    private String data;

    // Specifies mappedBy to use the same join table as specified by
    // TeacherSpring.
    @ManyToMany(mappedBy = "students")
    private List<TeacherSpring> teachers = new ArrayList<>();

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
     * @return the teachers
     */
    public List<TeacherSpring> getTeachers() {
        return Collections.unmodifiableList(teachers);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Remove teacher from student. This is package private since it doesn't
     * take care of the inverse relationship.
     * 
     * @param teacherSpring
     * @return true if the teacher was related to this student
     */
    boolean removeTeacherInternal(TeacherSpring teacher) {
        return teachers.remove(teacher);
    }

    /**
     * remove teacher from student and take care of the bidirectional
     * relationship.
     * 
     * @param teacher
     *            the teacher to remove
     * @return true if the teacher was related to this student
     */
    public boolean removeTeacher(TeacherSpring teacher) {
        teacher.removeStudentInternal(this);
        return removeTeacherInternal(teacher);
    }

    /**
     * Add teacher to student. This is package private since it doesn't take
     * care of the inverse relationship.
     * 
     * @param teacher
     *            the teacher to add
     */
    void addTeacherInternal(TeacherSpring teacher) {
        teachers.add(teacher);

    }

}
