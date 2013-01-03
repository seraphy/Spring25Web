package spring25web.form;

import java.io.Serializable;
import java.util.Date;


public class PersonEditForm implements Serializable {

    private static final long serialVersionUID = -863757228578187265L;

    private int id;
    
    private String name;
    
    private Date birthday;
    
    private Double height;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("(id=").append(id);
        buf.append(", name=").append(name);
        buf.append(", birthday=").append(birthday);
        buf.append(", height=").append(height);
        buf.append(")");
        return buf.toString();
    }
}
