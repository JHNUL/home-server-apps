package org.juhanir.message_server.utils;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Class that is capable of inserting large-ish datasets into TimescaleDB
 * from CSV files using COPY inserts.
 */
public class PerfTestSeeder {

    private final String dbUrl;
    private final String username;
    private final String password;
    private static final String DATA_FOLDER = "seed_data";
    private static final String COPY_SQL = "COPY %s FROM STDIN WITH CSV";

    /**
     * Create new test seeder instance.
     *
     * @param dbUrl    the database url fit for jdbc, so must begin with jdbc:
     * @param username database username
     * @param password database password
     */
    public PerfTestSeeder(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    private void seedDataFromResourceFile(String tableName, String seedDataFileName) {
        String source = DATA_FOLDER + "/" + seedDataFileName;
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Reader reader = new BufferedReader(
                     new InputStreamReader(
                             Objects.requireNonNull(
                                     getClass()
                                             .getClassLoader()
                                             .getResourceAsStream(source))
                     ))
        ) {
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            CopyManager copyManager = pgConn.getCopyAPI();
            copyManager.copyIn(COPY_SQL.formatted(tableName), reader);
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Failed COPY seeding", e);
        }
    }

    public void seedData() {
        seedDataFromResourceFile("sensor.device", "device.csv");
        seedDataFromResourceFile("sensor.humidity_status", "humidity_status.csv");
    }

}
