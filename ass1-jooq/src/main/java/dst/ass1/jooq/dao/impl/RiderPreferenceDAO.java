package dst.ass1.jooq.dao.impl;

import dst.ass1.jooq.dao.IRiderPreferenceDAO;
import dst.ass1.jooq.model.IRiderPreference;
import dst.ass1.jooq.model.public_.tables.Preference;
import dst.ass1.jooq.model.public_.tables.RiderPreference;
import org.jooq.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.jooq.impl.DSL.*;


public class RiderPreferenceDAO implements IRiderPreferenceDAO {
    private final DSLContext context;
    private static final RiderPreference RIDER_PREFERENCE = RiderPreference.RIDER_PREFERENCE;
    private static final Preference PREFERENCE = Preference.PREFERENCE;

    public RiderPreferenceDAO(DSLContext dslContext) {
        this.context = dslContext;
    }

    @Override
    public List<IRiderPreference> findAll() {

        var riderRecords =
                context.select(
                                RIDER_PREFERENCE.RIDER_ID,
                                RIDER_PREFERENCE.AREA,
                                RIDER_PREFERENCE.VEHICLE_CLASS,
                                multisetAgg(
                                        PREFERENCE.PREF_KEY,
                                        PREFERENCE.PREF_VALUE
                                ).as("preferences")
                        )
                        .from(RIDER_PREFERENCE)
                        .join(PREFERENCE)
                        .on(PREFERENCE.RIDER_ID.eq(RIDER_PREFERENCE.RIDER_ID))
                        .groupBy(RIDER_PREFERENCE)
                        .fetch();

        return new ArrayList<>(riderRecords.map(this::mapRecordToRider));
    }

    @Override
    public IRiderPreference findById(Long id) {

        var riderRecord =
                context.select(
                                RIDER_PREFERENCE.RIDER_ID,
                                RIDER_PREFERENCE.AREA,
                                RIDER_PREFERENCE.VEHICLE_CLASS,
                                multisetAgg(
                                        PREFERENCE.PREF_KEY,
                                        PREFERENCE.PREF_VALUE
                                ).as("preferences")
                        )
                        .from(RIDER_PREFERENCE)
                        .join(PREFERENCE)
                        .on(PREFERENCE.RIDER_ID.eq(RIDER_PREFERENCE.RIDER_ID))
                        .where(RIDER_PREFERENCE.RIDER_ID.eq(id))
                        .groupBy(RIDER_PREFERENCE)
                        .fetchOne();

        if (riderRecord == null) {
            return null;
        }

        return mapRecordToRider(riderRecord);
    }

    @Override
    public IRiderPreference insert(IRiderPreference model) {

        context.transaction((Configuration trx) -> {

            trx.dsl().insertInto(RIDER_PREFERENCE, RIDER_PREFERENCE.RIDER_ID, RIDER_PREFERENCE.AREA,
                            RIDER_PREFERENCE.VEHICLE_CLASS)
                    .values(model.getRiderId(), model.getArea(), model.getVehicleClass()).execute();

            var modelPreferences = model.getPreferences();

            for (String key : modelPreferences.keySet()) {
                trx.dsl().insertInto(PREFERENCE,
                                PREFERENCE.RIDER_ID, PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE)
                        .values(model.getRiderId(), key, modelPreferences.get(key)).execute();
            }

        });

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

        context.transaction((Configuration trx) -> {
            for (String key : modelPreferences.keySet()
            ) {
                trx.dsl().mergeInto(PREFERENCE)
                        .using(context.selectOne())
                        .on(PREFERENCE.PREF_KEY.eq(key))
                        .whenMatchedThenUpdate()
                        .set(PREFERENCE.PREF_VALUE, modelPreferences.get(key))
                        .whenNotMatchedThenInsert(PREFERENCE.PREF_KEY, PREFERENCE.PREF_VALUE, PREFERENCE.RIDER_ID)
                        .values(key, modelPreferences.get(key), riderId)
                        .execute();
            }
        });
    }

    private IRiderPreference
    mapRecordToRider(Record4<Long, String, String, Result<Record2<String, String>>> riderResult) {

        var rider = new dst.ass1.jooq.model.impl.RiderPreference();

        rider.setRiderId(riderResult.get(RIDER_PREFERENCE.RIDER_ID));
        rider.setVehicleClass(riderResult.get(RIDER_PREFERENCE.VEHICLE_CLASS));
        rider.setArea(riderResult.get(RIDER_PREFERENCE.AREA));

        var preferences = (List<Record2<String, String>>) riderResult.get("preferences");

        if (preferences == null) {
            return rider;
        }

        var preferenceMap = new HashMap<String, String>();
        for (Record2<String, String> preferenceTuple : preferences) {
            preferenceMap.put(preferenceTuple.value1(), preferenceTuple.value2());

        }
        rider.setPreferences(preferenceMap);
        return rider;
    }
}
