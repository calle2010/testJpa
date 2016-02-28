package testJpa.simpleTable.dao;

import java.util.List;

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
    public SimpleTable save(final SimpleTable entity) {
        // EntityManager em = emf.createEntityManager();
        final SimpleTable st = em.merge(entity);
        // em.flush();
        // em.clear();
        return st;
    }

    @Override
    public SimpleTable findOne(final Long id) {
        // EntityManager em = emf.createEntityManager();

        final SimpleTable entity = em.find(SimpleTable.class, id);

        return entity;
    }

    @Override
    public List<SimpleTable> findAll() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);

        final TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public long count() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(SimpleTable.class)));

        final TypedQuery<Long> tq = em.createQuery(cq);

        return tq.getSingleResult();
    }

    @Override
    public void delete(final SimpleTable entity) {
        // EntityManager em = emf.createEntityManager();

        em.remove(entity);
    }

    @Override
    public boolean exists(final Long id) {
        // EntityManager em = emf.createEntityManager();

        return null != em.find(SimpleTable.class, id);

    }

    @Override
    public boolean isEmpty() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.select(root.get("id"));
        final TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return CollectionUtils.isEmpty(tq.getResultList());
    }

    @Override
    public List<SimpleTable> findByData(final String data) {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);
        final Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.where(cb.equal(root.get("data"), data));

        final TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

}
