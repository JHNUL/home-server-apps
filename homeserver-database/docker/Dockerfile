FROM liquibase/liquibase:4.32
LABEL authors="juhanir"

USER root

RUN apt update && apt install -y postgresql-client && apt clean

COPY ./wait-for-db.sh /wait-for-db.sh
RUN chown liquibase:liquibase /wait-for-db.sh \
    && chmod 0744 /wait-for-db.sh

USER liquibase