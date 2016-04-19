package testJpa.simple.table.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * a simple table without relationships
 */
@Entity
@Table(name = "SIMPLE_TABLE")
public class SimpleTable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SIMPLE_TABLE_ID")
    @SequenceGenerator(name = "SEQ_SIMPLE_TABLE_ID", sequenceName = "SEQ_SIMPLE_TABLE_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

    @Size(min = 3, message = "the data must have at least {min} characters, but '${validatedValue}' is shorter")
    private String data;

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

}
