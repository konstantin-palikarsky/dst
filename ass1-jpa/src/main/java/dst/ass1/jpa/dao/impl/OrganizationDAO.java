package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.IOrganizationDAO;
import dst.ass1.jpa.model.IDriver;
import dst.ass1.jpa.model.IOrganization;

import java.util.List;

public class OrganizationDAO implements IOrganizationDAO {
    @Override
    public IOrganization findById(Long id) {
        return null;
    }

    @Override
    public List<IOrganization> findAll() {
        return null;
    }

    @Override
    public IDriver findTopAndActiveDriverByOrganization(long organizationId) {
        return null;
    }
}
