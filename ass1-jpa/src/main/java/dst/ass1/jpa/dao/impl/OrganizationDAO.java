package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IOrganization;
import dst.ass1.jpa.model.impl.Organization;
import dst.ass1.jpa.util.Constants;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class OrganizationDAO extends BasicDAOImpl<IOrganization> implements IOrganizationDAO {

    public OrganizationDAO(EntityManager em) {
        super(Organization.class, em);
    }

    @Override
    public IDriver findTopAndActiveDriverByOrganization(long organizationId) {
        try {
            return (IDriver) this.em.createNamedQuery(Constants.Q_TOP_DRIVER_OF_ORGANIZATION)
                    .setParameter("organization_id", organizationId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
