package testJpa.simpleSpring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import testJpa.TestJpaTestConfiguration;
import testJpa.simpleSpring.dao.ParentSpringDao;
import testJpa.simpleSpring.domain.ChildSpring;
import testJpa.simpleSpring.domain.ParentSpring;

/**
 * Test CRUD functionality of a parent/child table.
 * <p>
 * Will not roll back after tests so that JPA executes the updates and DBUnit
 * picks up the changes.
 * <p>
 * All methods which update data need a @Transactional annotation together
 * with @DirtiesContext so that the entity manager etc. will be fresh for the
 * next test. This is required since DBUnit will update the test data but the
 * entity manager will still have outdated entries.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestJpaTestConfiguration.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class, TransactionalTestExecutionListener.class })
@Rollback(false)
@DirtiesContext
public class ParentSpringTest {

    @Autowired
    ParentSpringDao dao;

    @Autowired
    EntityManagerFactory emf;

    private static final Logger LOGGER = LoggerFactory.getLogger(ParentSpringTest.class);

    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    public void testFindAll() {
        final List<ParentSpring> list = dao.findAll();

        assertEquals(3, list.size());

        // Assert child elements are lazily loaded.

        // EclipseLink logging level
        // FINE should show four SELECTs: one for the parent table and one per
        // parent record to retrieve the children (n+1). See also
        // testBatchFetch().
        for (ParentSpring pt : list) {
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(pt, "children"));
            assertEquals(3, pt.getChildren().size());
        }

    }

    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    public void testFindByData() {
        final List<ParentSpring> list = dao.findByData("one thousand");

        assertEquals(1, list.size());
        assertEquals(10001000, list.get(0).getId().longValue());
        // Assert children are (lazyly) loaded.
        assertEquals(3, list.get(0).getChildren().size());
    }

    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @ExpectedDatabase(value = "expect_ParentSpring_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_ChildSpring_parent_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testRemoveParentManaged() {
        final ParentSpring st = dao.findOne(10001000l);
        assertNotNull("entity to delete must not be null", st);

        dao.delete(st);

        assertNull("most not find deleted entity", dao.findOne(10001000l));
        assertEquals("must be one entry less", 2, dao.count());

    }

    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @ExpectedDatabase(value = "expect_ParentSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_ChildSpring.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateParentManaged() {
        LOGGER.info("start test update managed");

        final ParentSpring st = dao.findOne(10001000l);

        st.setData("updated");

        LOGGER.info("end test update managed");
    }

    /**
     * Update parent table entry from an unmanaged object. While this works, it
     * is not a good practice.
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @ExpectedDatabase(value = "expect_ParentSpring_updated.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "setup_ChildSpring.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testUpdateParentUnmanaged() {
        LOGGER.info("start test update unmanaged");
        final ParentSpring st = new ParentSpring();
        st.setId(10001000l);
        st.setData("updated");

        // child entry has to be added as well, otherwise orphanRemoval will
        // just remove it
        // when parent table entry is merged
        final ChildSpring ct1 = new ChildSpring();
        ct1.setId(20001000l);
        ct1.setData("two thousand");
        st.addChild(ct1);

        final ChildSpring ct2 = new ChildSpring();
        ct2.setId(20001010l);
        ct2.setData("two thousand ten");
        st.addChild(ct2);

        final ChildSpring ct3 = new ChildSpring();
        ct3.setId(20001020l);
        ct3.setData("two thousand twenty");
        st.addChild(ct3);

        dao.save(st);
        LOGGER.info("end test update unmanaged");
    }

    /**
     * removing the child by modifying the collection is not allowed: children
     * are managed by the owning entity parent
     */
    @Test(expected = UnsupportedOperationException.class)
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testRemoveChildWrong() {
        final ParentSpring st = dao.findOne(10001000l);
        st.getChildren().remove(0);
    }

    /**
     * child object will be removed by orphan removal
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @ExpectedDatabase(value = "setup_ParentSpring.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_ChildSpring_deleted.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testRemoveChild() {
        final ParentSpring st = dao.findOne(10001000l);
        final int numberOfChildren = st.getChildren().size();

        Optional<ChildSpring> ct = st.getChildren().stream().filter(i -> i.getId().equals(20001000l)).findFirst();

        st.removeChild(ct.get());

        assertEquals(numberOfChildren - 1, st.getChildren().size());
    }

    /**
     * add one child object
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    @ExpectedDatabase(value = "setup_ParentSpring.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @ExpectedDatabase(value = "expect_ChildSpring_created.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    @DirtiesContext
    public void testCreateChild() {
        final ParentSpring st = dao.findOne(10001000l);

        final ChildSpring newChild = new ChildSpring();
        newChild.setData("new child");
        /*
         * Since it is a new child the id property is left initial; it will be
         * set by sequence later. The parent id is set by the parent entity to
         * ensure consistency.
         */
        st.addChild(newChild);
    }

    /**
     * Test batch fetching all parents. See FINE logging level of EclipseLink to
     * verify there are only two SELECTs.
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    public void testBatchFetchAll() {
        final List<ParentSpring> st = dao.findAllBatchFetch();

        assertEquals(3, st.size());

        // assert children are lazily loaded, but after first access all are
        // loaded (batch fetching).
        boolean first = true;
        for (ParentSpring pt : st) {
            boolean childrenLoaded = emf.getPersistenceUnitUtil().isLoaded(pt, "children");
            // either first or children are loaded
            assert (first ^ childrenLoaded);
            first = false;

            assertEquals(3, pt.getChildren().size());
        }

    }

    /**
     * Test batch fetching parents by data. See FINE logging level of
     * EclipseLink to verify there are only two SELECTs.
     */
    @Test
    @DatabaseSetup("setup_ParentSpring.xml")
    @DatabaseSetup("setup_ChildSpring.xml")
    public void testBatchFetchByData() {
        final List<ParentSpring> st = dao.findByDataBatchFetch("one thousand");
        assertEquals(1, st.size());

        // assert children are lazily loaded, but after first access all are
        // loaded (batch fetching).
        boolean first = true;
        for (ParentSpring pt : st) {
            boolean childrenLoaded = emf.getPersistenceUnitUtil().isLoaded(pt, "children");
            // either first or children are loaded
            assert (first ^ childrenLoaded);
            first = false;
            assertEquals(3, pt.getChildren().size());
        }

    }

}
