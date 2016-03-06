package testJpa.spring.parentChild.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import testJpa.TestJpaTestConfiguration;
import testJpa.spring.parentChild.domain.ParentSpring;

/**
 * test specific aspects of transaction and lazy loading
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class })
@Rollback(false)
public class ParentSpringTransactionalTest {
    private PersistenceUnitUtil puu;

    @Autowired
    private ParentSpringDao dao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        puu = em.getEntityManagerFactory().getPersistenceUnitUtil();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ParentSpringTransactionalTest.class);

    /**
     * Test aspects of transactions, caching, and lazy loading
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @Transactional
    public void testTransactional() {
        final List<ParentSpring> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        final ParentSpring find1 = list.get(0);
        assertEquals(10001000, find1.getId().longValue());

        assertTrue("entity is managed", em.contains(find1));

        // finding the same entity again yields the same object and doesn't
        // SELECT again (cache!)
        final ParentSpring find2 = dao.findOne(find1.getId());
        assertSame(find1, find2);
    }

    /**
     * test aspects of lazy loading and caching outside of a transaction
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    public void testNonTransactional() {
        final List<ParentSpring> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        final ParentSpring find1 = list.get(0);
        assertEquals(10001000, find1.getId().longValue());

        // entity is not managed, since no transaction context was handed to the
        // find method
        assertTrue("entity is not managed", !em.contains(find1));
        // finding the same entity again yields another object, but doesn't
        // SELECT again (cache!)
        final ParentSpring find2 = dao.findOne(find1.getId());
        // object is different
        assertNotSame(find1, find2);
        // still the properties are the same objects!
        assertSame(find1.getData(), find2.getData());
        assertSame(find1.getId(), find2.getId());

        // children are not yet loaded (lazy loading) on both
        assertFalse(puu.isLoaded(find1, "children"));
        assertFalse(puu.isLoaded(find2, "children"));

        // now load the children of first object
        assertEquals(3, find1.getChildren().size());

        // and assert that children are loaded on first object only
        assertTrue(puu.isLoaded(find1, "children"));
        assertFalse(puu.isLoaded(find2, "children"));

        // now load the children on second object: No SELECT is done because of
        // cache
        assertEquals(3, find2.getChildren().size());

        // and assert that children are loaded on second object, too
        assertTrue(puu.isLoaded(find2, "children"));

    }

    /**
     * EclipseLink, unlike Hibernate, allows lazy loading of entities after the
     * entity manager is closed.
     * 
     * @see <a href=
     *      "https://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#sthref57">
     *      Understanding EclipseLink concepts - Container Managed Persistence
     *      Contexts</a>
     * 
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @Transactional
    public void testLazyLoadAfterTransaction() {
        final List<ParentSpring> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        final ParentSpring find1 = list.get(0);
        assertEquals(10001000, find1.getId().longValue());

        assertTrue("entity is managed", em.contains(find1));
        assertTrue("children are lazy loaded", !puu.isLoaded(find1, "children"));

        // Never do this in production. This is to get the underlying
        // EntityManager instance since Spring's proxy will always return true
        // for the isOpen() method.
        //
        // See org.springframework.orm.jpa.SharedEntityManagerCreator
        EntityManager realEm = ((EntityManagerProxy) em).getTargetEntityManager();

        // End the transaction. This should close the entity manager.
        assertTrue("entity manager is open", realEm.isOpen());
        TestTransaction.end();
        assertFalse("entity manager is closed", realEm.isOpen());

        // now load the children
        assertEquals(3, find1.getChildren().size());
        /*
         * This creates another SELECT /after/ the children are read from the
         * database to read the parent entity again. This seems to be happening
         * due to the many-to-one relationship of children to parent. To
         * minimize database accesses one should not rely on EclipseLink's
         * special functionality to read lazily loaded properties after the
         * transaction and entity manager is closed.
         */
    }
}
