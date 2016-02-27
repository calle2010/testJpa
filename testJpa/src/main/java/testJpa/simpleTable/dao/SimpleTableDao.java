package testJpa.simpleTable.dao;

import testJpa.simpleTable.domain.SimpleTable;

/**
 * Data access object for a simple table. The interface is modeled after
 * Spring's CrudRepository interface.
 */
public interface SimpleTableDao {

    /**
     * save an entity
     *
     * @param entity
     *            to save
     * @return the managed entity
     */
    SimpleTable save(SimpleTable entity);

    /**
     * find one entity by id
     *
     * @param id
     *            to find
     * @return the entity
     */
    SimpleTable findOne(long id);

    /**
     * Retrieve all entities from the table. No specific order is guaranteed.
     *
     * @return all entities
     */
    Iterable<SimpleTable> findAll();

    /**
     * Get count of entities in the table.
     * <p>
     * Use {@link SimpleTableDao#isEmpty()} instead if count would be check for
     * zero only.
     *
     * @return count of entities in the table
     */
    Long count();

    /**
     * delete one entity
     *
     * @param entity
     *            to delete
     */
    void delete(SimpleTable entity);

    /**
     * check if one entity exists in the table
     *
     * @param id
     *            to find
     * @return true if entity exists
     */
    boolean exists(long id);

    /**
     * (not in CrudRepository) more efficient method than
     * {@link SimpleTableDao#count()} to determine if the table is empty
     *
     * @return true if there is no entry in the table
     */
    boolean isEmpty();

    /**
     * find by data field
     * 
     * @param data
     *            the data to find
     * @return the matching entities
     */
    Iterable<SimpleTable> findByData(String data);

}