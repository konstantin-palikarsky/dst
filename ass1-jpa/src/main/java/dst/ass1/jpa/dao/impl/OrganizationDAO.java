package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.impl.Organization;

import javax.persistence.EntityManager;

public class OrganizationDAO extends FinderDaoImpl<IOrganization> implements IOrganizationDAO {

    public OrganizationDAO( EntityManager em) {
        super(Organization.class, em);
    }

    @Override
    public IDriver findTopAndActiveDriverByOrganization(long organizationId) {
        return null;
    }
}
