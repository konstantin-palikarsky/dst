package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IEmploymentDAO;
import dst.ass1.jpa.model.IEmployment;
import dst.ass1.jpa.model.impl.Employment;

import javax.persistence.EntityManager;

public class EmploymentDAO extends FinderDaoImpl<IEmployment, Employment> implements IEmploymentDAO {
    public EmploymentDAO(EntityManager em) {
        super(Employment.class, em);
    }
}
