package testJpa.simpleTable.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * a simple table without relationships
 */
@Entity(name = "SIMPLE_TABLE")
public class SimpleTable {

    @Id
    private long id;

    private String data;

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
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
