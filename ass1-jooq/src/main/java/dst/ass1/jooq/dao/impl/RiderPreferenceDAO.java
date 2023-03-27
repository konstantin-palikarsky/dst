package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.public_.tables.Preference;
import dst.ass1.jooq.model.public_.tables.RiderPreference;
import dst.ass1.jooq.model.public_.tables.records.PreferenceRecord;
import dst.ass1.jooq.model.public_.tables.records.RiderPreferenceRecord;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.List;


public class RiderPreferenceDAO implements IRiderPreferenceDAO {
    private final DSLContext context;

    public RiderPreferenceDAO(DSLContext dslContext) {
        this.context = dslContext;
    }


    @Override
    public IRiderPreference findById(Long id) {
        RiderPreferenceRecord riderRecord =
                context.fetchOne(RiderPreference.RIDER_PREFERENCE, RiderPreference.RIDER_PREFERENCE.RIDER_ID
                        .eq(id));

        if (riderRecord == null) {
            return null;
        }
        var specificPreferences =
                context.fetch(Preference.PREFERENCE,
                        Preference.PREFERENCE.RIDER_ID.eq(id));

        var rider = new dst.ass1.jooq.model.impl.RiderPreference();
        rider.setRiderId(riderRecord.get(RiderPreference.RIDER_PREFERENCE.RIDER_ID));
        rider.setArea(riderRecord.get(RiderPreference.RIDER_PREFERENCE.AREA));
        rider.setVehicleClass(riderRecord.get(RiderPreference.RIDER_PREFERENCE.VEHICLE_CLASS));

        if (specificPreferences.isNotEmpty()) {
            var preferenceMap = new HashMap<String, String>();

            for (PreferenceRecord preferenceRecord : specificPreferences) {
                preferenceMap.put(preferenceRecord.get(Preference.PREFERENCE.PREF_KEY),
                        preferenceRecord.get(Preference.PREFERENCE.PREF_VALUE));
            }

            rider.setPreferences(preferenceMap);
        }

        return rider;
    }

    @Override
    public List<IRiderPreference> findAll() {
        return null;
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {
        var RIDER_PREFERENCE = RiderPreference.RIDER_PREFERENCE;
        var PREFERENCE = Preference.PREFERENCE;


        context.insertInto(RIDER_PREFERENCE, RIDER_PREFERENCE.RIDER_ID, RIDER_PREFERENCE.AREA,
                        RIDER_PREFERENCE.VEHICLE_CLASS)
                .values(model.getRiderId(), model.getArea(), model.getVehicleClass()).execute();

        var modelPreferences = model.getPreferences();

        for (String key : modelPreferences.keySet()
        ) {
            context.insertInto(PREFERENCE,
                            PREFERENCE.RIDER_ID, PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE)
                    .values(model.getRiderId(), key, modelPreferences.get(key)).execute();
        }

        return model;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void updatePreferences(IRiderPreference model) {

    }
}
