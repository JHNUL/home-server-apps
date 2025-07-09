CREATE TABLE sensor.humidity_status (
    device_id INTEGER NOT NULL,
    measurement_time TIMESTAMPTZ,
    component_id INTEGER NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

-- composite primary key
ALTER TABLE sensor.humidity_status ADD PRIMARY KEY (device_id, measurement_time);

-- device_id is a foreign key reference, if a device is deleted
-- its measurements are also deleted
ALTER TABLE sensor.humidity_status
    ADD CONSTRAINT device_id_fkey
    FOREIGN KEY (device_id)
    REFERENCES sensor.device(id)
    ON DELETE CASCADE;
