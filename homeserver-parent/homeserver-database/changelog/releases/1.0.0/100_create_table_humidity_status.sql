CREATE TABLE sensor.humidity_status (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    device_id INTEGER NOT NULL,
    measurement_time TIMESTAMPTZ,
    component_id INTEGER NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

-- device_id is a foreign key reference
ALTER TABLE sensor.humidity_status
    ADD CONSTRAINT device_id_fkey
    FOREIGN KEY (device_id)
    REFERENCES sensor.device(id);
