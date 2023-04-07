package xyz.raitaki.rdungeons;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.raitaki.rdungeons.commands.GenerateCommand;
import xyz.raitaki.rdungeons.utils.DungeonUtils;

import java.io.File;

public final class RDungeons extends JavaPlugin {

    @Getter private static RDungeons instance;
    @Getter private static File schemFolder;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        getCommand("generateDungeon").setExecutor(new GenerateCommand());
        schemFolder = new File(RDungeons.getInstance().getDataFolder() + "/rooms/schematics");
        DungeonUtils.loadRooms();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
