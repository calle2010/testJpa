package testJpa.simpleTable.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import testJpa.simpleTable.domain.SimpleTable;

@Repository
public class SimpleTableDaoImpl implements SimpleTableDao {

    @PersistenceContext
    EntityManager em;

    // @PersistenceContext
    // EntityManager em;

    @Override
    public SimpleTable save(SimpleTable entity) {
        // EntityManager em = emf.createEntityManager();
        SimpleTable st = em.merge(entity);
        // em.flush();
        // em.clear();
        return st;
    }

    @Override
    public SimpleTable findOne(long id) {
        // EntityManager em = emf.createEntityManager();

        SimpleTable entity = em.find(SimpleTable.class, id);

        return entity;
    }

    @Override
    public Iterable<SimpleTable> findAll() {
        // EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);

        TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public Long count() {
        // EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(SimpleTable.class)));

        TypedQuery<Long> tq = em.createQuery(cq);

        return tq.getSingleResult();
    }

    @Override
    public void delete(SimpleTable entity) {
        // EntityManager em = emf.createEntityManager();

        em.remove(entity);
    }

    @Override
    public boolean exists(long id) {
        // EntityManager em = emf.createEntityManager();

        return null != em.find(SimpleTable.class, id);

    }

    @Override
    public boolean isEmpty() {
        // EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.select(root.get("id"));
        TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return CollectionUtils.isEmpty(tq.getResultList());
    }

    @Override
    public Iterable<SimpleTable> findByData(String data) {
        // EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);
        Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.where(cb.equal(root.get("data"), data));

        TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

}
