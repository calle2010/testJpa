package testJpa.simpleTable.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * a child table with many-to-one relationship to parent
 */
@Entity
@Table(name = "CHILD_TABLE")
public class ChildTable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CHILD_TABLE_ID")
    @SequenceGenerator(name = "SEQ_CHILD_TABLE_ID", sequenceName = "SEQ_CHILD_TABLE_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

    private String data;

    @ManyToOne
    private ParentTable parent;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final String data) {
        this.data = data;
    }

    /**
     * @return the parent
     */
    public ParentTable getParent() {
        return parent;
    }

    /**
     * Setting the parent is package private since no user of this class but the
     * parent is allowed to override the parent.
     * 
     * @param parent
     *            the parent to set
     */
    void setParent(ParentTable parent) {
        this.parent = parent;
    }

}
