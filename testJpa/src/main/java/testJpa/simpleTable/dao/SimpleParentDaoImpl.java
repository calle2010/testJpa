package testJpa.simpleTable.dao;

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
import org.eclipse.persistence.config.QueryHints;
import org.springframework.stereotype.Repository;

import testJpa.simpleTable.domain.ParentTable;

@Repository
public class SimpleParentDaoImpl implements SimpleParentDao {

    @PersistenceContext
    EntityManager em;

    // @PersistenceContext
    // EntityManager em;

    @Override
    public ParentTable save(final ParentTable entity) {
        // EntityManager em = emf.createEntityManager();
        final ParentTable st = em.merge(entity);
        // em.flush();
        // em.clear();
        return st;
    }

    @Override
    public ParentTable findOne(final Long id) {
        // EntityManager em = emf.createEntityManager();

        final ParentTable entity = em.find(ParentTable.class, id);

        return entity;
    }

    @Override
    public List<ParentTable> findAll() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<ParentTable> cq = cb.createQuery(ParentTable.class);

        final TypedQuery<ParentTable> tq = em.createQuery(cq);

        return tq.getResultList();
    }

    @Override
    public long count() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(ParentTable.class)));

        final TypedQuery<Long> tq = em.createQuery(cq);

        return tq.getSingleResult();
    }

    @Override
    public void delete(final ParentTable entity) {
        // EntityManager em = emf.createEntityManager();

        em.remove(entity);
    }

    @Override
    public boolean exists(final Long id) {
        // EntityManager em = emf.createEntityManager();

        return null != em.find(ParentTable.class, id);

    }

    @Override
    public boolean isEmpty() {
        // EntityManager em = emf.createEntityManager();

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<ParentTable> root = cq.from(ParentTable.class);
        cq.select(root.get("id"));
        final TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return CollectionUtils.isEmpty(tq.getResultList());
    }

    @Override
    public List<ParentTable> findByData(final String data) {
        // EntityManager em = emf.createEntityManager();

        final TypedQuery<ParentTable> tq = createDataQuery(data);

        return tq.getResultList();
    }

    private TypedQuery<ParentTable> createDataQuery(final String data) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<ParentTable> cq = cb.createQuery(ParentTable.class);
        final Root<ParentTable> root = cq.from(ParentTable.class);
        cq.where(cb.equal(root.get("data"), data));

        final TypedQuery<ParentTable> tq = em.createQuery(cq);
        return tq;
    }

    @Override
    public void deleteAllInBatch() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaDelete<ParentTable> cq = cb.createCriteriaDelete(ParentTable.class);
        final Root<ParentTable> root = cq.from(ParentTable.class);

        final Query tq = em.createQuery(cq);

        tq.executeUpdate();
    }

    @Override
    public ParentTable findOneBatchFetch(long id) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<ParentTable> cq = cb.createQuery(ParentTable.class);
        Root<ParentTable> parent = cq.from(ParentTable.class);
        cq.where(cb.equal(parent.get("id"), id));

        final TypedQuery<ParentTable> tq = em.createQuery(cq);
        // This is EclipseLink specific. By default it creates a join fetch.
        tq.setHint(QueryHints.BATCH, "ParentTable.children");

        return tq.getSingleResult();
    }

    @Override
    public List<ParentTable> findAllBatchFetch() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<ParentTable> cq = cb.createQuery(ParentTable.class);

        final TypedQuery<ParentTable> tq = em.createQuery(cq);
        // This is EclipseLink specific. By default it creates a join fetch.
        tq.setHint(QueryHints.BATCH, "ParentTable.children");

        return tq.getResultList();
    }

    @Override
    public List<ParentTable> findByDataBatchFetch(String data) {

        TypedQuery<ParentTable> q = createDataQuery(data);
        // This is EclipseLink specific. By default it creates a join fetch.
        q.setHint(QueryHints.BATCH, "ParentTable.children");

        return q.getResultList();
    }
}
