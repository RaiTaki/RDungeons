package xyz.raitaki.rdungeons.utils;

import org.bukkit.Bukkit;
import xyz.raitaki.rdungeons.RDungeons;
import xyz.raitaki.rdungeons.dungeon.Direction;
import xyz.raitaki.rdungeons.dungeon.Room;

import java.io.File;
import java.util.*;

import static xyz.raitaki.rdungeons.dungeon.Direction.*;

public class DungeonUtils {

    private static Direction getRotatedDirection(Direction direct, int x, int y, int z){
        switch (direct) {
            case UP -> {
                if (x != 0) {
                    if (x == 90)
                        return SOUTH;
                    else if (x == 180)
                        return DOWN;
                    else if (x == 270)
                        return Direction.NORTH;
                }
                if (z != 0) {
                    if (z == 90)
                        return Direction.WEST;
                    else if (z == 180)
                        return DOWN;
                    else if (z == 270)
                        return EAST;
                }

                return Direction.UP;
            }
            case DOWN -> {
                if (x != 0) {
                    if (x == 90)
                        return Direction.NORTH;
                    else if (x == 180)
                        return Direction.UP;
                    else if (x == 270)
                        return SOUTH;
                }
                if (z != 0) {
                    if (z == 90)
                        return EAST;
                    else if (z == 180)
                        return Direction.UP;
                    else if (z == 270)
                        return Direction.WEST;
                }

                return DOWN;
            }
            case EAST -> {
                if (y != 0) {
                    if (y == 90)
                        return Direction.NORTH;
                    else if (y == 180)
                        return Direction.WEST;
                    else if (y == 270)
                        return SOUTH;
                }
                if (z != 0) {
                    if (z == 90)
                        return Direction.UP;
                    else if (z == 180)
                        return Direction.WEST;
                    else if (z == 270)
                        return DOWN;
                }

                return EAST;
            }
            case WEST -> {
                if (y != 0) {
                    if (y == 90)
                        return SOUTH;
                    else if (y == 180)
                        return EAST;
                    else if (y == 270)
                        return Direction.NORTH;
                }
                if (z != 0) {
                    if (z == 90)
                        return DOWN;
                    else if (z == 180)
                        return EAST;
                    else if (z == 270)
                        return Direction.UP;
                }

                return Direction.WEST;
            }
            case NORTH -> {
                if (x != 0) {
                    if (x == 90)
                        return Direction.UP;
                    else if (x == 180)
                        return SOUTH;
                    else if (x == 270)
                        return DOWN;
                }
                if (y != 0) {
                    if (y == 90)
                        return Direction.WEST;
                    else if (y == 180)
                        return SOUTH;
                    else if (y == 270)
                        return EAST;
                }

                return Direction.NORTH;
            }
            case SOUTH -> {
                if (x != 0) {
                    if (x == 90)
                        return DOWN;
                    else if (x == 180)
                        return Direction.NORTH;
                    else if (x == 270)
                        return Direction.UP;
                }
                if (y != 0) {
                    if (y == 90)
                        return EAST;
                    else if (y == 180)
                        return Direction.NORTH;
                    else if (y == 270)
                        return Direction.WEST;
                }

                return SOUTH;
            }
            default -> {
                return direct;
            }
        }
    }

    public static void loadRooms(){
        File folder = RDungeons.getSchemFolder();
        if(!folder.exists()) {
            folder.mkdirs();
        }
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for(File file : listOfFiles) {
            boolean isLift = false;
            List<Direction> directions = new ArrayList<>();
            if (file.getName().endsWith(".schem")) {
                String name = file.getName().replace(".schem", "").toLowerCase();
                for(String data : name.split("_")){
                    if(data.equals("lift")){
                        isLift = true;
                        continue;
                    }
                    if(data.contains("dungeon"))
                        continue;
                    if (!directions.contains(Direction.valueOf(data.toUpperCase()))) {
                        directions.add(Direction.valueOf(data.toUpperCase()));
                    }
                }
            }
            Room.addRoom(new Room(file.getName().replace(".schem", "").toLowerCase(), 0,0,0, isLift, directions));

            for (int i = 1; i != 4; i++) {
                List<Direction> newDirectionsX = rotateDirections(directions, i * 90, 0, 0);
                List<Direction> newDirectionsY = rotateDirections(directions, 0, i * 90, 0);
                List<Direction> newDirectionsZ = rotateDirections(directions, 0, 0, i * 90);
                List<Direction> newDirectionsXY = rotateDirections(directions, i * 90, i * 90, 0);
                List<Direction> newDirectionsXZ = rotateDirections(directions, i * 90, 0, i * 90);
                List<Direction> newDirectionsYZ = rotateDirections(directions, 0, i * 90, i * 90);

                boolean finalIsLift = isLift;
                List<Room> rooms = Room.getLoadedRooms();

                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsX.stream().sorted().toList()) && finalIsLift == room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),i*90, 0, 0, isLift, newDirectionsX));
                }
                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsY.stream().sorted().toList()) && finalIsLift == room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),0, i*90, 0, isLift, newDirectionsY));
                }
                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsZ.stream().sorted().toList()) && finalIsLift == room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),0, 0, i*90, isLift, newDirectionsZ));
                }
            }
        }

        Bukkit.getLogger().warning(TextUtil.getColored("&aLoaded " + Room.getLoadedRooms().size() + " rooms"));
    }

    public static List<Direction> rotateDirections(List<Direction> directions, int xRotation, int yRotation, int zRotation) {
        List<Direction> newDirections = new ArrayList<>();
        for (Direction direction : directions) {
            Direction rotatedDirection = getRotatedDirection(direction, xRotation, yRotation, zRotation);
            newDirections.add(rotatedDirection);
        }
        return newDirections;
    }

    public static Room getRandomRoom(List<Direction> directions, boolean isLift){
        List<Room> rooms = Room.getLoadedRooms();
        List<Room> possibleRooms = new ArrayList<>();
        for(Room room : rooms){
            if(room.isLift() == isLift){
                if(new HashSet<>(room.getDirections()).containsAll(directions)){
                    possibleRooms.add(room);
                }
            }
        }
        if(possibleRooms.size() == 0){
            return null;
        }
        return possibleRooms.get(new Random().nextInt(possibleRooms.size()));
    }

    public static Room getRandomLiftRoom(){
        List<Room> rooms = Room.getLoadedRooms();
        List<Room> possibleRooms = new ArrayList<>();
        for(Room room : rooms){
            if(room.isLift()){
                possibleRooms.add(room);
            }
        }
        if(possibleRooms.size() == 0){
            return null;
        }
        if(possibleRooms.size() == 1){
            return possibleRooms.get(0);
        }
        return possibleRooms.get(new Random().nextInt(possibleRooms.size()));
    }

    public static Room getRandomRoom(List<Direction> possibleDirections, List<Direction> blockedDirections){
        List<Room> rooms = Room.getLoadedRooms();
        List<Room> possibleRooms = new ArrayList<>();
        for(Room room : rooms){
            if(room.isLift()){
                continue;
            }
            if(new HashSet<>(room.getDirections()).containsAll(possibleDirections)){
                if(!room.getDirections().stream().anyMatch(blockedDirections::contains)){

                    possibleRooms.add(room);
                }
            }
        }

        Bukkit.broadcastMessage("Possible rooms: " + possibleRooms.size());

        if(possibleRooms.size() == 0){
            return null;
        }
        if(possibleRooms.size() == 1){
            return possibleRooms.get(0);
        }
        return possibleRooms.get(new Random().nextInt(possibleRooms.size()));
    }
}
