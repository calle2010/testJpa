package testJpa.spring.table.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import testJpa.spring.table.domain.SpringTable;

/**
 * implementation for custom queries for table SPRING_TABLE
 */
@Component
public class SpringTableDaoImpl implements SpringTableDaoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public boolean isEmpty() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<SpringTable> root = cq.from(SpringTable.class);
        cq.select(root.get("id"));
        final TypedQuery<Long> tq = em.createQuery(cq);
        tq.setMaxResults(1);

        return CollectionUtils.isEmpty(tq.getResultList());
    }

}
