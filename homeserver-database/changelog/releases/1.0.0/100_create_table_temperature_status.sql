CREATE TABLE sensor.temperature_status (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    device_id INTEGER NOT NULL,
    measurement_time TIMESTAMPTZ,
    component_id INTEGER NOT NULL,
    value_celsius DOUBLE PRECISION NOT NULL,
    value_fahrenheit DOUBLE PRECISION NOT NULL
);

-- device_id is a foreign key reference, if a device is deleted
-- its measurements are also deleted
ALTER TABLE sensor.temperature_status
    ADD CONSTRAINT device_id_fkey
    FOREIGN KEY (device_id)
    REFERENCES sensor.device(id)
    ON DELETE CASCADE;
