# Homeserver UI

User interface for homeserver functionality.

## Getting started

Create the file `homeserver-ui/.env.local` with the correct values for these variables:
```txt
VITE_KEYCLOAK_URL=
VITE_KEYCLOAK_REALM=
VITE_KEYCLOAK_CLIENT_ID=
```

Start the whole backend stack with:
```sh
docker compose -f docker/docker-compose-server.yml up -d --remove-orphans --force-recreate
```

This starts all the backend components. If developing message-server simultaneously, start
with the `docker-compose.yml` file in the same location and then start message-server normally
with `mvn quarkus:dev`.

