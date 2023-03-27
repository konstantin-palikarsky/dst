/*
 In the initialize phase this script will be called to delete and recreate your tables.

 Afterwards in the generate-sources phase jooq will scan your existing tables in your database and
 generate sources from it.
 */

-- This will delete the tables every time the sql script is called
DROP TABLE IF EXISTS preference, rider_preference;

-- Add here your sql statements to create the tables "preference" and "rider_preference"
CREATE TABLE rider_preference
(
    rider_id      BIGINT NOT NULL PRIMARY KEY ,
    vehicle_class VARCHAR(255),
    area          VARCHAR(255)
);

CREATE TABLE preference
(
    id         IDENTITY NOT NULL PRIMARY KEY,
    rider_id   BIGINT,
    pref_key   VARCHAR(255),
    pref_value VARCHAR(255),
    FOREIGN KEY (rider_id) REFERENCES rider_preference (rider_id)
        ON DELETE CASCADE
);

