# Homeserver UI

User interface for homeserver functionality.

## Getting started

Create the file `homeserver-ui/.env.local` with the correct values for these variables:
```txt
VITE_KEYCLOAK_URL
VITE_KEYCLOAK_REALM
VITE_KEYCLOAK_CLIENT_ID
VITE_MESSAGE_SERVER_API_URL
```

Start the whole backend stack with:
```sh
docker compose -f docker/docker-compose-server.yml up -d --remove-orphans --force-recreate
```

This starts all the backend components. If developing message-server simultaneously, start
with the `docker-compose.yml` file in the same location and then start message-server normally
with `mvn quarkus:dev`.

The keycloak realm used for local development and tests creates two users at initialization
that can be used out of the box:
- testuser:testuser123 (has role user)
- testadmin:testadmin123 (has role admin)

Run `npm run dev` to start the UI in development mode (hot reload and all dat jive).

To seed some test data, use `mqtt_seeder.sh` (requires mosquitto CLI tools).

Tear down cleanly (removes volumes) with:
```sh
docker compose -f docker/docker-compose-server.yml down --remove-orphans --volumes
```

To persist data across runs, teardown without the `--volumes` option.

## Data visualization

Uses [Recharts](https://recharts.github.io/en-US/api/) components.