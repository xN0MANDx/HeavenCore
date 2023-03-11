package pl.nomand.heavencore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class MySQLDataSource {

    private final HikariDataSource dataSource;

    public MySQLDataSource(MySQL data) {

        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + data.getHost() + ":" + 3306 + "/" + data.getDatabase() + "?useSSL=false");
        hikariConfig.setUsername(data.getUsername());
        hikariConfig.setPassword(data.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setMaximumPoolSize(2);
        hikariConfig.setIdleTimeout(30000L);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (final SQLException exception) {
            Bukkit.getLogger().throwing("DatabaseConnection", "getConnection", exception);
            return null;
        }
    }

    public ResultSet query(final String query, final Object... queryReplacements) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 1; i <= queryReplacements.length; i++) {
                statement.setObject(i, queryReplacements[i - 1]);
            }

            return statement.executeQuery();
        } catch (final SQLException exception) {
            Bukkit.getLogger().throwing("DatabaseConnection", "query", exception);
            return null;
        }
    }

    public void query(final String query, final Consumer<ResultSet> handler, final Object... queryReplacements) {
        handler.accept(this.query(query, queryReplacements));
    }

    public int update(final String update, final Object... updateReplacements) {
        try (final Connection connection = this.getConnection();
             final PreparedStatement statement = connection.prepareStatement(update)) {

            for (int i = 1; i <= updateReplacements.length; i++) {
                statement.setObject(i, updateReplacements[i - 1]);
            }

            return statement.executeUpdate();
        } catch (final SQLException exception) {
            Bukkit.getLogger().throwing("DatabaseConnection", "update", exception);
            return -1;
        }
    }

}