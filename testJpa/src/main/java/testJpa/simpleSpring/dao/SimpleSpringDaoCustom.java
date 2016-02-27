package testJpa.simpleSpring.dao;

/**
 * custom behavior for Spring JpaRepository of table SIMPLE_SPRING
 */
public interface SimpleSpringDaoCustom {
    /**
     * (not in CrudRepository) more efficient method than
     * {@link SimpleSpringDao#count()} to determine if the table is empty
     *
     * @return true if there is no entry in the table
     */
    boolean isEmpty();

}
