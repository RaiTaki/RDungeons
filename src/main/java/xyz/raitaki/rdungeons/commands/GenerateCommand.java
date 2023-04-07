package xyz.raitaki.rdungeons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.raitaki.rdungeons.DungeonGenerator;
import xyz.raitaki.rdungeons.dungeon.Room;

import java.util.HashMap;

public class GenerateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player p))
            return false;

        HashMap<Vector, Room> rooms = DungeonGenerator.generateDungeon(15);
        DungeonGenerator.pasteDungeon(rooms, p.getLocation());
        return false;
    }
}
