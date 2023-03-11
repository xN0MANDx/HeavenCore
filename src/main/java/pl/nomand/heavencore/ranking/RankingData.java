package pl.nomand.heavencore.ranking;

import ch.njol.skript.variables.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.nomand.heavencore.common.HeavenCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RankingData {

    private final HeavenCore main;

    private Player player;

    private String targetId;
    private long level;
    private String guildName;

    // Multi Players

    public RankingData(HeavenCore main) {
        this.main = main;
    }

    public void updateForEveryone() {
        new BukkitRunnable() {
            public void run() {

                Connection connection = main.getHeavenConfig().getMySQLDataSource().getConnection();

                try {

                    assert connection != null;

                    connection.setAutoCommit(false);

                    for(Player player : Bukkit.getOnlinePlayers()) {

                        String targetId = player.getName().toLowerCase();
                        long level;
                        String guildName;

                        Object object = Variables.getVariable("rpg-core.level::" + targetId, null, false);
                        if (object == null) {
                            level = 0L;
                        } else {
                            level = (long) object;
                        }

                        guildName = (String) Variables.getVariable("x.pklan::" + targetId, null, false);
                        if (guildName == null)
                            guildName = "";

                        PreparedStatement statement = connection.prepareStatement("UPDATE `ranking` SET `level`=" + level + ",`guild`='" + guildName + "' WHERE `name`='" + targetId + "'");

                        statement.addBatch();
                        statement.executeBatch();
                    }

                    connection.commit();
                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(main);
    }

    // Single Player

    public RankingData(HeavenCore main, Player player) {
        this.main = main;
        this.player = player;
        this.targetId = player.getName().toLowerCase();

        Object object = Variables.getVariable("rpg-core.level::" + targetId, null, false);
        if (object == null) {
            this.level = 0L;
        } else {
            this.level = (long) object;
        }

        this.guildName = (String) Variables.getVariable("x.pklan::" + targetId, null, false);

        if (this.guildName == null)
            this.guildName = "";
    }

    public void create() {
        new BukkitRunnable() {
            public void run() {

                Connection connection = main.getHeavenConfig().getMySQLDataSource().getConnection();

                try {

                    assert connection != null;

                    connection.setAutoCommit(false);

                    PreparedStatement statement = connection.prepareStatement("INSERT INTO `ranking`(`id`, `name`, `level`, `guild`) VALUES (NULL, '"+player.getName()+"', "+level+", '"+guildName+"')");

                    statement.addBatch();
                    statement.executeBatch();

                    connection.commit();
                    connection.setAutoCommit(true);

                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(main);
    }

    public void update() {
        new BukkitRunnable() {
            public void run() {

                Connection connection = main.getHeavenConfig().getMySQLDataSource().getConnection();

                try {

                    assert connection != null;

                    connection.setAutoCommit(false);

                    PreparedStatement statement = connection.prepareStatement("UPDATE `ranking` SET `level`=" + level + ",`guild`='" + guildName + "' WHERE `name`='" + targetId + "'");

                    statement.addBatch();
                    statement.executeBatch();

                    connection.commit();
                    connection.setAutoCommit(true);

                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(main);
    }

    public long getLevel() {
        return level;
    }

    public String getGuildName() {
        return guildName;
    }

}
