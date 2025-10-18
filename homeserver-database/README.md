# Database schema

This project contains the database schema and liquibase changelogs to create it.

## Docker image

A custom image for database migration. It contains the liquibase base image and some customization to enable waiting for TimescaleDB to be ready.

## Releases

Image is pushed to docker hub repository `juhanir/liquibase-migrator`.

## Conventions

For sensor data, the timestamp should be consistently named `measurement_time`.

## Query performance

Time series data should be queried _fast_. That means storing it in hypertables with appropriate indices and chunking intervals. Hypertable information is found in the `timescaledb_information` schema that TimescaleDB creates.

Queries can be analyzed e.g. in the following way:

```sql
EXPLAIN ANALYZE statement;
```

Where statement is an arbitrary query.

For example
```sql
EXPLAIN ANALYZE SELECT * FROM sensor.humidity_status
WHERE measurement_time >= '2025-06-06T06:06:06Z'
AND measurement_time <= '2025-06-06T06:26:06Z';
```

where the output is something like this:
```txt
Index Scan using _hyper_2_1_chunk_humidity_status_measurement_time_idx on _hyper_2_1_chunk
(cost=0.28..2.89 rows=21 width=24) (actual time=0.010..0.012 rows=21 loops=1)
  Index Cond: ((measurement_time >= '2025-06-06 09:06:06+03'::timestamp with time zone) AND (measurement_time <= '2025-06-06 09:26:06+03'::timestamp with time zone))
Planning Time: 0.223 ms
Execution Time: 0.029 ms
```

The previous example is legit. It shows that indices are being used and the correct chunk is being accessed, and no others.
