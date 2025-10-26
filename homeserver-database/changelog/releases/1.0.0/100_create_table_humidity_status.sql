CREATE TABLE sensor.humidity_status (
    identifier VARCHAR(255) NOT NULL,
    measurement_time TIMESTAMPTZ,
    component_id INTEGER NOT NULL,
    value DOUBLE PRECISION NOT NULL
);

-- composite primary key
ALTER TABLE sensor.humidity_status ADD PRIMARY KEY (identifier, measurement_time);

-- identifier is a foreign key reference, if a device is deleted
-- its measurements are also deleted
ALTER TABLE sensor.humidity_status
    ADD CONSTRAINT identifier_fkey
    FOREIGN KEY (identifier)
    REFERENCES sensor.device(identifier)
    ON DELETE CASCADE;


-- compound index on identifier and measurement time
CREATE INDEX hs_device_measurement_time_idx ON sensor.humidity_status (identifier, measurement_time);

-- convert to hypertable
SELECT create_hypertable('sensor.humidity_status', by_range('measurement_time'));
