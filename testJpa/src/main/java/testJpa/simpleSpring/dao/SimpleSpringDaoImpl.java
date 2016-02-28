package testJpa.simpleSpring.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import testJpa.simpleSpring.domain.SimpleSpring;

/**
 * implementation for custom queries for table SIMPLE_SPRING
 */
@Component
public class SimpleSpringDaoImpl implements SimpleSpringDaoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public boolean isEmpty() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<SimpleSpring> root = cq.from(SimpleSpring.class);
        cq.select(root.get("id"));
        final TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return CollectionUtils.isEmpty(tq.getResultList());
    }

}
