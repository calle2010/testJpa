package testJpa.simpleSpring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import testJpa.simpleSpring.domain.SimpleSpring;

/**
 * Data access object for a simple table. The interface is modeled after
 * Spring's CrudRepository interface.
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
    Iterable<SimpleSpring> findByData(String data);

}