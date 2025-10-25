# Homeserver Keycloak

Provides Identity and Access Management (IAM) services.

Done based on https://www.keycloak.org/server/containers.

Port for health and metrics endpoint is 9000 and the admin console port is 8443.

## Building the image

Run the following command:
`docker build -t juhanir/homeserver-keycloak:<TAG> -f docker/Dockerfile .`


## Environment variables

When running the image set the following environment variables:
```shell
KC_DB=postgres
KC_HTTP_MANAGEMENT_SCHEME=http
KC_HTTP_PORT=8888
KC_HTTP_ENABLED=true
KC_HOSTNAME=http://localhost:8888
KC_HOSTNAME_BACKCHANNEL_DYNAMIC=true
KC_DB_URL=
KC_DB_USERNAME=
KC_DB_PASSWORD=
KC_BOOTSTRAP_ADMIN_USERNAME=
KC_BOOTSTRAP_ADMIN_PASSWORD=
```

https://www.keycloak.org/server/all-config?options-filter=all

## Releases

Image is pushed to docker hub repository `juhanir/homeserver-keycloak`.
