package testJpa.simpleSpring.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.eclipse.persistence.config.QueryHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import testJpa.simpleSpring.domain.ParentSpring;

/**
 * Data access object for a parent table.
 */
@Repository
public interface ParentSpringDao extends JpaRepository<ParentSpring, Long> {

    /**
     * find by data field
     *
     * @param data
     *            the data to find
     * @return the matching entities
     */
    List<ParentSpring> findByData(String data);

    /**
     * find by data field of child
     *
     * @param data
     *            the data to find
     * @return the matching entities
     */
    List<ParentSpring> findByChildrenDataLike(String data);

    /**
     * batch fetch children
     * 
     * @return all parents
     */
    @Query("select ps from ParentSpring ps")
    @org.springframework.data.jpa.repository.QueryHints(value = {
            @QueryHint(name = QueryHints.BATCH, value = "ParentSpring.children") })
    List<ParentSpring> findAllBatchFetch();

    /**
     * batch fetch children
     * 
     * @return parents matching data
     */
    @Query("select ps from ParentSpring ps where ps.data = ?1")
    @org.springframework.data.jpa.repository.QueryHints(value = {
            @QueryHint(name = QueryHints.BATCH, value = "ParentSpring.children") })
    List<ParentSpring> findByDataBatchFetch(String data);

}