package pl.nomand.heavencore.common;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.nomand.heavencore.database.MySQL;
import pl.nomand.heavencore.database.MySQLDataSource;

import java.io.File;
import java.io.IOException;

public class HeavenConfig {

    private MySQLDataSource mySQLDataSource;
    private final int autosave;

    public HeavenConfig(HeavenCore main) {
        File file = new File(main.getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(main.getResource("config.yml"), file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

//        this.mySQLDataSource = new MySQLDataSource(new MySQL(yml.getString("mysql.host"), yml.getString("mysql.database"), yml.getString("mysql.user"), yml.getString("mysql.password")));
        this.autosave = yml.getInt("autosave");

        Bukkit.getLogger().info("[HeavenCore] Config zostal wczytany.");
    }

    public MySQLDataSource getMySQLDataSource() {
        return mySQLDataSource;
    }

    public int getAutosave() {
        return autosave;
    }
}
