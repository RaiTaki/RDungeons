package xyz.raitaki.rdungeons.dungeon;

import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Room {

    @Getter private static List<Room> loadedRooms = new ArrayList<>();

    @Getter private List<Direction> directions;
    @Getter private final String schematic;
    @Getter private final int rotationX;
    @Getter private final int rotationY;
    @Getter private final int rotationZ;
    @Getter private boolean isLift;

    public Room(String schematic, int rotationX, int rotationY, int rotationZ, boolean isLift, List<Direction> directions){
        this.directions = new ArrayList<>();
        this.schematic = schematic;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.isLift = isLift;
        this.directions = directions;
    }

    public void addDirection(Direction direction){
        if(!directions.contains(direction))
            directions.add(direction);
    }

    public static void addRoom(Room room){
        loadedRooms.add(room);
    }

}
