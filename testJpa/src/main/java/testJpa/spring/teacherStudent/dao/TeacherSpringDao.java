package testJpa.spring.teacherStudent.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import testJpa.spring.teacherStudent.domain.TeacherSpring;

/**
 * Data access object for the teacher table.
 */
@Repository
public interface TeacherSpringDao extends JpaRepository<TeacherSpring, Long> {

}