package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import org.jooq.DSLContext;

import java.util.List;

public class RiderPreferenceDAO implements IRiderPreferenceDAO {
    private final DSLContext dslContext;

    public RiderPreferenceDAO(DSLContext dslContext) {
        this.dslContext = dslContext;
    }


    @Override
    public IRiderPreference findById(Long id) {
        return null;
    }

    @Override
    public List<IRiderPreference> findAll() {
        return null;
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void updatePreferences(IRiderPreference model) {

    }
}
