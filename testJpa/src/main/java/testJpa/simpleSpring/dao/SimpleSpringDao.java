package testJpa.simpleSpring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import testJpa.simpleSpring.domain.SimpleSpring;

/**
 * Data access object for a simple table.
 */
@Repository
public interface SimpleSpringDao extends JpaRepository<SimpleSpring, Long>, SimpleSpringDaoCustom {

    /**
     * find by data field
     *
     * @param data
     *            the data to find
     * @return the matching entities
     */
    List<SimpleSpring> findByData(String data);

}