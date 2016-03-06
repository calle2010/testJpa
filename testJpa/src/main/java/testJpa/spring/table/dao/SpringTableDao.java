package testJpa.spring.table.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import testJpa.spring.table.domain.SpringTable;

/**
 * Data access object for a simple table.
 */
@Repository
public interface SpringTableDao extends JpaRepository<SpringTable, Long>, SpringTableDaoCustom {

    /**
     * find by data field
     *
     * @param data
     *            the data to find
     * @return the matching entities
     */
    List<SpringTable> findByData(String data);

}