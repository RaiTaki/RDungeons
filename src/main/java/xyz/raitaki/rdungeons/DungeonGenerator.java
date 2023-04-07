package xyz.raitaki.rdungeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import xyz.raitaki.rdungeons.dungeon.Direction;
import xyz.raitaki.rdungeons.dungeon.Room;
import xyz.raitaki.rdungeons.utils.DungeonUtils;
import xyz.raitaki.rdungeons.utils.RandomCollection;
import xyz.raitaki.rdungeons.utils.Schematicutils;

import java.util.*;

public class DungeonGenerator {
    public static final int MINE_ROOM_SIZE = 31;

    public static HashMap<Vector, Room> generateDungeon(int roomAmount){
        HashMap<Vector, Room> dungeon = new HashMap<>();
        dungeon.put(new Vector(0,0,0), DungeonUtils.getRandomLiftRoom());

        while(dungeon.size() != roomAmount){
            Vector point = null;

            for (Vector possiblePoint : dungeon.keySet()) {
                Room room = dungeon.get(possiblePoint);
                if(room == null)
                    continue;

                Set<Direction> possibleDirections = new LinkedHashSet<>(room.getDirections());

                for (Direction possibleDirection : dungeon.get(possiblePoint).getDirections()) {
                    Vector possiblePoint2 = new Vector(possiblePoint.getBlockX() + possibleDirection.getX(),
                                                      possiblePoint.getBlockY() + possibleDirection.getY(),
                                                      possiblePoint.getBlockZ() + possibleDirection.getZ());
                    if (dungeon.containsKey(possiblePoint2)) {
                        possibleDirections.remove(possibleDirection);
                    }
                }

                if (!possibleDirections.isEmpty()) {
                    point = possiblePoint;
                }
            }
            if (point == null)
                continue;

            RandomCollection<Direction> randomCollection = new RandomCollection<>();
            LinkedList<Direction> possibleDirections = new LinkedList<>(dungeon.get(point).getDirections());
            LinkedList<Direction> possibleBlockedDirections = new LinkedList<>();
            LinkedList<Direction> removedDirections = new LinkedList<>();

            if(possibleDirections.isEmpty())
                continue;

            for(Direction possibleDirection : possibleDirections) {

                Vector possiblePoint = new Vector(point.getBlockX() + possibleDirection.getX(),
                                                  point.getBlockY() + possibleDirection.getY(),
                                                  point.getBlockZ() + possibleDirection.getZ());
                if (dungeon.containsKey(possiblePoint) && !removedDirections.contains(possibleDirection)) {
                    removedDirections.add(possibleDirection);
                    possibleBlockedDirections.add(possibleDirection);
                }

            }

            possibleDirections.removeAll(removedDirections);
            for(Direction possibleDirection : possibleDirections){
                randomCollection.add(100.0/possibleBlockedDirections.size(), possibleDirection);
            }

            Direction direction = randomCollection.next();
            Vector newPoint = new Vector(point.getBlockX() + direction.getX(),
                                         point.getBlockY() + direction.getY(),
                                         point.getBlockZ() + direction.getZ());
            Room room = DungeonUtils.getRandomRoom(possibleDirections, possibleBlockedDirections);
            dungeon.put(newPoint, room);

            point = null;
        }
        DungeonUtils.fixRooms(dungeon);

        return dungeon;
    }

    public static void pasteDungeon(HashMap<Vector, Room> dungeon, Location loc){
        Bukkit.getLogger().warning("Pasting dungeon at " + loc.toString());
        Location debugLocation = loc.clone();
        dungeon.forEach((point, room) -> {
            Location pasteLoc = loc.clone().add(point.getBlockX()* MINE_ROOM_SIZE, (point.getBlockY()* MINE_ROOM_SIZE),point.getBlockZ()* MINE_ROOM_SIZE);
            Schematicutils.pasteSchem(pasteLoc,
                    room.getSchematic(),
                    room.getRotationX(),
                    room.getRotationY(),
                    room.getRotationZ());
        });
    }
}
