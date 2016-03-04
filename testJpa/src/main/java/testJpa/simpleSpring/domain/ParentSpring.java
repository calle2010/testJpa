package testJpa.simpleSpring.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * a parent table with one-to-many "owning" relationship to child
 */
@Entity
@Table(name = "PARENT_SPRING")
public class ParentSpring {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARENT_SPRING_ID")
    @SequenceGenerator(name = "SEQ_PARENT_SPRING_ID", sequenceName = "SEQ_PARENT_SPRING_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

    private String data;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildSpring> children = new ArrayList<>();

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
     * @return the children
     */
    public List<ChildSpring> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * @param child
     *            the child to add
     * @return true
     */
    public boolean addChild(ChildSpring child) {
        child.setParent(this);
        return children.add(child);
    }

    /**
     * @param child
     *            the child to remove
     * @return true if child was removed
     */
    public boolean removeChild(ChildSpring child) {
        return children.remove(child);
    }

}
