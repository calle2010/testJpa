package testJpa.spring.table.dao;

/**
 * custom behavior for Spring JpaRepository of table SPRING_TABLE
 */
public interface SpringTableDaoCustom {
    /**
     * (not in CrudRepository) more efficient method than
     * {@link SpringTableDao#count()} to determine if the table is empty
     *
     * @return true if there is no entry in the table
     */
    boolean isEmpty();

}
