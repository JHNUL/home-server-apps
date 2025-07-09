CREATE TABLE sensor.temperature_status (
    device_id INTEGER NOT NULL,
    measurement_time TIMESTAMPTZ,
    component_id INTEGER NOT NULL,
    value_celsius DOUBLE PRECISION NOT NULL,
    value_fahrenheit DOUBLE PRECISION NOT NULL
);

-- composite primary key
ALTER TABLE sensor.temperature_status ADD PRIMARY KEY (device_id, measurement_time);

-- device_id is a foreign key reference, if a device is deleted
-- its measurements are also deleted
ALTER TABLE sensor.temperature_status
    ADD CONSTRAINT device_id_fkey
    FOREIGN KEY (device_id)
    REFERENCES sensor.device(id)
    ON DELETE CASCADE;


-- compound index on device id and measurement time
CREATE INDEX ts_device_measurement_time_idx ON sensor.temperature_status (device_id, measurement_time);

-- convert to hypertable
SELECT create_hypertable('sensor.temperature_status', by_range('measurement_time'));