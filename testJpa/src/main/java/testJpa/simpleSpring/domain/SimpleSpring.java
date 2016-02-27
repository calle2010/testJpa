package testJpa.simpleSpring.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;

/**
 * a simple table without relationships
 */
@Entity(name = "SIMPLE_SPRING")
public class SimpleSpring {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SIMPLE_SPRING_ID")
    @SequenceGenerator(name = "SEQ_SIMPLE_SPRING_ID", sequenceName = "SEQ_SIMPLE_SPRING_ID", allocationSize = 50, initialValue = 50)
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
    public void setId(Long id) {
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
    public void setData(String data) {
        this.data = data;
    }

}
