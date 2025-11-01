-- Creating one table for all signals leads to sparse
-- data which is bad for spatial efficiency and indices.
-- EAV model would work so long as signals are of the
-- same type, which they won't be. Sparse model it is.
CREATE TABLE sensor.signal_data (
    identifier VARCHAR(255) NOT NULL,
    measurement_time TIMESTAMPTZ NOT NULL,
    temperature_celsius DOUBLE PRECISION,
    temperature_fahrenheit DOUBLE PRECISION,
    relative_humidity DOUBLE PRECISION
);

-- composite primary key
ALTER TABLE sensor.signal_data ADD PRIMARY KEY (identifier, measurement_time);

-- identifier is a foreign key reference, devices cannot be deleted if they have data.
ALTER TABLE sensor.signal_data
    ADD CONSTRAINT identifier_fkey
    FOREIGN KEY (identifier)
    REFERENCES sensor.device(identifier)
    ON DELETE RESTRICT;

-- compound index on identifier and measurement time
CREATE INDEX ts_device_measurement_time_idx ON sensor.signal_data (identifier, measurement_time);

-- convert to hypertable
SELECT create_hypertable('sensor.signal_data', by_range('measurement_time'));
