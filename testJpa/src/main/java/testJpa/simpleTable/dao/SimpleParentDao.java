package testJpa.simpleTable.dao;

import java.util.List;

import testJpa.simpleTable.domain.ParentTable;

/**
 * Data access object for a simple table. The interface is modeled after
 * Spring's CrudRepository interface.
 */
public interface SimpleParentDao {

    /**
     * save an entity
     *
     * @param entity
     *            to save
     * @return the managed entity
     */
    ParentTable save(ParentTable entity);

    /**
     * find one entity by id
     *
     * @param id
     *            to find
     * @return the entity
     */
    ParentTable findOne(Long id);

    /**
     * Retrieve all entities from the table. No specific order is guaranteed.
     *
     * @return all entities
     */
    List<ParentTable> findAll();

    /**
     * Get count of entities in the table.
     * <p>
     * Use {@link SimpleParentDao#isEmpty()} instead if count would be check for
     * zero only.
     *
     * @return count of entities in the table
     */
    long count();

    /**
     * delete one entity
     *
     * @param entity
     *            to delete
     */
    void delete(ParentTable entity);

    /**
     * check if one entity exists in the table
     *
     * @param id
     *            to find
     * @return true if entity exists
     */
    boolean exists(Long id);

    /**
     * (not in CrudRepository) more efficient method than
     * {@link SimpleParentDao#count()} to determine if the table is empty
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
    List<ParentTable> findByData(String data);

    /**
     * Delete all entries with update query. This has to run in a separate
     * transaction since the EntityManager may not be aware of the bulk updates.
     */
    void deleteAllInBatch();

    /**
     * This method will fetch the parent object and corresponding child object
     * in two SELECTs instead of n+1.
     * 
     * @param l
     * 
     * @return the parent table entry
     */
    ParentTable findOneBatchFetch(long id);

    /**
     * This method will fetch all parent objects and corresponding child object
     * in two SELECTs instead of m+m*n.
     * 
     * @return the parent table entry with all children fetched
     */
    List<ParentTable> findAllBatchFetch();

    /**
     * @param data
     *            the data to retrieve
     * @return the parent records with matching data
     */
    List<ParentTable> findByDataBatchFetch(String data);

}