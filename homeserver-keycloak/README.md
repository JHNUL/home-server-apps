# Homeserver Keycloak

Provides Identity and Access Management (IAM) services.

Done based on https://www.keycloak.org/server/containers.

## Building the image

Run the following command:
`docker build -t juhanir/homeserver-keycloak:<TAG> -f docker/Dockerfile .`


## Environment variables

When running the image set the following environment variables:
```shell
KC_DB=postgres
KC_HOSTNAME=localhost
KC_DB_URL=<DBURL>
KC_DB_USERNAME=<DBUSERNAME>
KC_DB_PASSWORD=<DBPASSWORD>
KC_BOOTSTRAP_ADMIN_USERNAME=<ADMIN USERNAME>
KC_BOOTSTRAP_ADMIN_PASSWORD=<ADMIN PASSWORD>
```

## Releases

Image is pushed to docker hub repository `juhanir/homeserver-keycloak`.