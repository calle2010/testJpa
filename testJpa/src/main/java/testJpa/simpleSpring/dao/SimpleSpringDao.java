package testJpa.simpleSpring.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import testJpa.simpleSpring.domain.SimpleSpring;

/**
 * Data access object for a simple table. The interface is modeled after
 * Spring's CrudRepository interface.
 */
@Repository
public interface SimpleSpringDao extends JpaRepository<SimpleSpring, Long> {

    /**
     * save an entity
     *
     * @param entity
     *            to save
     * @return the managed entity
     */
    @Override
    SimpleSpring save(SimpleSpring entity);

    /**
     * find one entity by id
     *
     * @param id
     *            to find
     * @return the entity
     */
    @Override
    SimpleSpring findOne(Long id);

    /**
     * Retrieve all entities from the table. No specific order is guaranteed.
     *
     * @return all entities
     */
    @Override
    List<SimpleSpring> findAll();

    /**
     * Get count of entities in the table.
     * <p>
     * Use {@link SimpleSpringDao#isEmpty()} instead if count would be check for
     * zero only.
     *
     * @return count of entities in the table
     */
    @Override
    long count();

    /**
     * delete one entity
     *
     * @param entity
     *            to delete
     */
    @Override
    void delete(SimpleSpring entity);

    /**
     * check if one entity exists in the table
     *
     * @param id
     *            to find
     * @return true if entity exists
     */
    @Override
    boolean exists(Long id);

    /**
     * (not in CrudRepository) more efficient method than
     * {@link SimpleSpringDao#count()} to determine if the table is empty
     *
     * @return true if there is no entry in the table
     */
    // boolean isEmpty();

    /**
     * find by data field
     * 
     * @param data
     *            the data to find
     * @return the matching entities
     */
    Iterable<SimpleSpring> findByData(String data);

}