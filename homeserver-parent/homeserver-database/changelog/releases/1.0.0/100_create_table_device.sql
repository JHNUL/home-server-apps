CREATE TABLE sensor.device (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    identifier VARCHAR(255),
    device_type INTEGER NOT NULL
);

-- identifier is unique
ALTER TABLE sensor.device ADD CONSTRAINT identifier_key UNIQUE (identifier);

-- device_type is a foreign-key reference
ALTER TABLE sensor.device
    ADD CONSTRAINT device_type_fkey
    FOREIGN KEY (device_type)
    REFERENCES sensor.device_type(id);
