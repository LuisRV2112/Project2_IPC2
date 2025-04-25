package utils;

import java.io.InputStream;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.username"));
            config.setPassword(prop.getProperty("db.password"));
            
            config.setMinimumIdle(Integer.parseInt(prop.getProperty("db.pool.minSize")));
            config.setMaximumPoolSize(Integer.parseInt(prop.getProperty("db.pool.maxSize")));
            config.setIdleTimeout(Long.parseLong(prop.getProperty("db.pool.maxIdleTime")));
            
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing database connection", e);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}