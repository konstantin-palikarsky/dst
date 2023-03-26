package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.dao.*;
import org.jooq.DSLContext;

public class DAOFactory implements IDAOFactory {

    private final DSLContext dslContext;

    public DAOFactory(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public IRiderPreferenceDAO createRiderPreferenceDao() {
        return new RiderPreferenceDAO(this.dslContext);
    }
}
