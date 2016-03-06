package testJpa.spring.table.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * a simple table without relationships
 */
@Entity
@Table(name = "SPRING_TABLE")
public class SpringTable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SPRING_TABLE_ID")
    @SequenceGenerator(name = "SEQ_SPRING_TABLE_ID", sequenceName = "SEQ_SPRING_TABLE_ID", allocationSize = 50, initialValue = 50)
    @OrderBy
    private Long id;

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
