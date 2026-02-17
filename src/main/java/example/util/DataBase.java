package example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBase {

    private static final HikariDataSource HIKARI_DATA_SOURCE;

    static {
        Dotenv dotenv = Dotenv.load();
        HikariConfig hikariConfig = new HikariConfig();

        String dbName = dotenv.get("DB_NAME");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        String url = "jdbc:postgresql://localhost:54321/" + dbName;

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);

        hikariConfig.setDriverClassName("org.postgresql.Driver");

        hikariConfig.setMaximumPoolSize(10);

        HIKARI_DATA_SOURCE = new HikariDataSource(hikariConfig);

        Runtime.getRuntime().addShutdownHook(
                new Thread(HIKARI_DATA_SOURCE::close)
        );
    }

    public static Connection getConnection() throws SQLException {
        return HIKARI_DATA_SOURCE.getConnection();
    }
}
