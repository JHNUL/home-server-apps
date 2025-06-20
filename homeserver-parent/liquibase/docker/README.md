# Docker image

A custom image for database migration. It contains the liquibase base image and some customization to enable waiting for TimescaleDB to be ready.

## Releases

Image is pushed to docker hub repository `juhanir/liquibase-migrator`.

### 1.0.0

Initial release using base image liquibase/liquibase:4.32.