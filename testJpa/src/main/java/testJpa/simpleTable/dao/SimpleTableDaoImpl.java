package testJpa.simpleTable.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import testJpa.simpleTable.domain.SimpleTable;

@Repository
public class SimpleTableDaoImpl implements SimpleTableDao {

    @PersistenceUnit
    EntityManagerFactory emf;

    @Override
    public SimpleTable save(SimpleTable entity) {
        EntityManager em = emf.createEntityManager();

        return em.merge(entity);
    }

    @Override
    public SimpleTable findOne(long id) {
        EntityManager em = emf.createEntityManager();

        return em.find(SimpleTable.class, id);
    }

    @Override
    public Iterable<SimpleTable> findAll() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);

        TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public Long count() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(SimpleTable.class)));

        TypedQuery<Long> tq = em.createQuery(cq);

        return tq.getSingleResult();
    }

    @Override
    public void delete(SimpleTable entity) {
        EntityManager em = emf.createEntityManager();

        em.remove(entity);
    }

    @Override
    public boolean exists(long id) {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.select(root.get("id"));

        TypedQuery<Long> tq = em.createQuery(cq);

        return null != tq.getSingleResult();
    }

    @Override
    public boolean isEmpty() {
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.select(root.get("id"));
        TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return null == tq.getSingleResult();
    }

}
