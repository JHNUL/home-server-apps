CREATE TABLE sensor.device_type (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL
);

-- name is unique
ALTER TABLE sensor.device_type ADD CONSTRAINT name_key UNIQUE (name);