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
    private static final RiderPreference RIDER_PREFERENCE = RiderPreference.RIDER_PREFERENCE;
    private static final Preference PREFERENCE = Preference.PREFERENCE;

    public RiderPreferenceDAO(DSLContext dslContext) {
        this.context = dslContext;
    }


    @Override
    public IRiderPreference findById(Long id) {
        RiderPreferenceRecord riderRecord =
                context.fetchOne(RIDER_PREFERENCE, RIDER_PREFERENCE.RIDER_ID
                        .eq(id));

        if (riderRecord == null) {
            return null;
        }
        var specificPreferences =
                context.fetch(PREFERENCE,
                        PREFERENCE.RIDER_ID.eq(id));

        var rider = new dst.ass1.jooq.model.impl.RiderPreference();
        rider.setRiderId(riderRecord.get(RIDER_PREFERENCE.RIDER_ID));
        rider.setArea(riderRecord.get(RIDER_PREFERENCE.AREA));
        rider.setVehicleClass(riderRecord.get(RIDER_PREFERENCE.VEHICLE_CLASS));

        if (specificPreferences.isNotEmpty()) {
            var preferenceMap = new HashMap<String, String>();

            for (PreferenceRecord preferenceRecord : specificPreferences) {
                preferenceMap.put(preferenceRecord.get(PREFERENCE.PREF_KEY),
                        preferenceRecord.get(PREFERENCE.PREF_VALUE));
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
        context.delete(RIDER_PREFERENCE)
                .where(RIDER_PREFERENCE.RIDER_ID.eq(id)).execute();
    }

    @Override
    public void updatePreferences(IRiderPreference model) {


        var modelPreferences = model.getPreferences();
        var riderId = model.getRiderId();

        for (String key : modelPreferences.keySet()
        ) {
            context.mergeInto(PREFERENCE)
                    .using(context.selectOne())
                    .on(PREFERENCE.PREF_KEY.eq(key))
                    .whenMatchedThenUpdate()
                    .set(PREFERENCE.PREF_VALUE, modelPreferences.get(key))
                    .whenNotMatchedThenInsert(PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE, PREFERENCE.RIDER_ID)
                    .values(key, modelPreferences.get(key), riderId)
                    .execute();
        }


    }
}
