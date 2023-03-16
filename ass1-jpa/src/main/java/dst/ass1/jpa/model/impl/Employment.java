package dst.ass1.jpa.model.impl;

import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.IEmploymentKey;
import org.hibernate.annotations.Target;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Date;


@Entity
public class Employment implements IEmployment {

    @EmbeddedId
    @Target(EmploymentKey.class)
    private IEmploymentKey id;

    private Date since;

    private Boolean active;

    @Override
    public IEmploymentKey getId() {
        return id;
    }

    @Override
    public Date getSince() {
        return since;
    }

    @Override
    public Boolean isActive() {
        return active;
    }

    @Override
    public void setId(IEmploymentKey id) {
        this.id = new EmploymentKey(id.getDriver(),id.getOrganization());
    }

    @Override
    public void setSince(Date since) {
        this.since = since;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }
}
