package testJpa.simpleTable.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.springframework.stereotype.Repository;

import testJpa.simpleTable.domain.SimpleTable;

@Repository
public class SimpleTableDaoImpl implements SimpleTableDao {

    @PersistenceUnit
    EntityManagerFactory emf;

    @Override
    public SimpleTable save(SimpleTable entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleTable findOne(long id) {
        EntityManager em = emf.createEntityManager();

        return em.find(SimpleTable.class, id);
    }

    @Override
    public Iterable<SimpleTable> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(SimpleTable entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exists(long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

}
