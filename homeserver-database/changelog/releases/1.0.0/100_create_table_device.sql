CREATE TABLE sensor.device (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    identifier VARCHAR(255),
    device_type INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    latest_communication TIMESTAMPTZ DEFAULT NOW(),
    is_disabled BOOLEAN DEFAULT false
);

-- identifier is unique
ALTER TABLE sensor.device ADD CONSTRAINT identifier_key UNIQUE (identifier);

-- device_type is a foreign-key reference
ALTER TABLE sensor.device
    ADD CONSTRAINT device_type_fkey
    FOREIGN KEY (device_type)
    REFERENCES sensor.device_type(id);
