package testJpa.simple.table.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import testJpa.simple.table.domain.SimpleTable;

@Repository
public class SimpleTableDaoImpl implements SimpleTableDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public SimpleTable save(final SimpleTable entity) {
        return em.merge(entity);
    }

    @Override
    public SimpleTable findOne(final Long id) {

        return em.find(SimpleTable.class, id);
    }

    @Override
    public List<SimpleTable> findAll() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);

        final TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public long count() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(SimpleTable.class)));

        final TypedQuery<Long> tq = em.createQuery(cq);

        return tq.getSingleResult();
    }

    @Override
    public void delete(final SimpleTable entity) {

        em.remove(entity);
    }

    @Override
    public boolean exists(final Long id) {

        return null != em.find(SimpleTable.class, id);

    }

    @Override
    public boolean isEmpty() {

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

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<SimpleTable> cq = cb.createQuery(SimpleTable.class);
        final Root<SimpleTable> root = cq.from(SimpleTable.class);
        cq.where(cb.equal(root.get("data"), data));

        final TypedQuery<SimpleTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public void deleteAllInBatch() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaDelete<SimpleTable> cq = cb.createCriteriaDelete(SimpleTable.class);
        final Query tq = em.createQuery(cq);

        tq.executeUpdate();
    }
}
