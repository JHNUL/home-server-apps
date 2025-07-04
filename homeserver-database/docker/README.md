# Docker image

A custom image for database migration. It contains the liquibase base image and some customization to enable waiting for TimescaleDB to be ready.

## Releases

Image is pushed to docker hub repository `juhanir/liquibase-migrator`.

### 1.0.0

Initial release using base image liquibase/liquibase:4.32.

### 1.0.1

Read database name from command url.

### 1.0.2

Read hostname from environment.

### 1.0.3

Not using LIQUIBASE_ as prefix for custom env variables.
