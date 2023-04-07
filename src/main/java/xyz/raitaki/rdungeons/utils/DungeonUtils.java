package xyz.raitaki.rdungeons.utils;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
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

            if(isLift)
                continue;
            for (int i = 1; i != 4; i++) {
                List<Direction> newDirectionsX = rotateDirections(directions, i * 90, 0, 0);
                List<Direction> newDirectionsY = rotateDirections(directions, 0, i * 90, 0);
                List<Direction> newDirectionsZ = rotateDirections(directions, 0, 0, i * 90);
                List<Direction> newDirectionsXY = rotateDirections(directions, i * 90, i * 90, 0);
                List<Direction> newDirectionsXZ = rotateDirections(directions, i * 90, 0, i * 90);
                List<Direction> newDirectionsYZ = rotateDirections(directions, 0, i * 90, i * 90);

                List<Room> rooms = Room.getLoadedRooms();

                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsX.stream().sorted().toList()) && !room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),i*90, 0, 0, false, newDirectionsX));
                }
                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsY.stream().sorted().toList()) && !room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),0, i*90, 0, false, newDirectionsY));
                }
                if (rooms.stream().noneMatch(room -> room.getDirections().stream().sorted().toList().equals(newDirectionsZ.stream().sorted().toList()) && !room.isLift())) {
                    rooms.add(new Room(file.getName().replace(".schem", ""),0, 0, i*90, false, newDirectionsZ));
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

        if(possibleRooms.size() == 0){
            return null;
        }
        if(possibleRooms.size() == 1){
            return possibleRooms.get(0);
        }
        return possibleRooms.get(new Random().nextInt(possibleRooms.size()));
    }

    public static Room getRandomRoom(int directionsAmount) {
        RandomCollection<Room> randomCollection = new RandomCollection<>();

        List<Room> possibleRooms = new ArrayList<>();
        for (Room room : Room.getLoadedRooms()) {
            if (room.getDirections().size() == directionsAmount) {
                possibleRooms.add(room);
            }
        }

        for (Room room : possibleRooms) {
            randomCollection.add(possibleRooms.size()/100.0, room);
        }

        return randomCollection.next();
    }

    public static void fixRooms(HashMap<org.bukkit.util.Vector, Room> dungeon){
        HashMap<org.bukkit.util.Vector, Room> toReplace = new HashMap<>();
        for (Map.Entry<org.bukkit.util.Vector, Room> entry1 : dungeon.entrySet()) {
            LinkedList<Direction> blockedDirections = new LinkedList<>();
            LinkedList<Direction> directions = new LinkedList<>();
            org.bukkit.util.Vector vector = entry1.getKey();
            Room room;

            for (Direction side : Direction.values()) {
                if (dungeon.containsKey(new Vector((vector.getBlockX() + side.getX()) + side.getX(), (vector.getBlockY()+ side.getY()) + side.getY(), (vector.getBlockZ() + side.getZ()) + side.getZ()))) {
                    if (!directions.contains(side)) {
                        directions.add(side);
                    }
                }
            }
            for (Direction side : Direction.values()) {
                if (!dungeon.containsKey(new Vector(vector.getBlockX() + side.getX(), vector.getBlockY() + side.getY(), vector.getBlockZ() + side.getZ()))) {
                    if(!blockedDirections.contains(side))
                        blockedDirections.add(side);
                }
            }

            directions.addAll(Arrays.stream(Direction.values()).toList().stream().filter(direction -> !blockedDirections.contains(direction)).toList());
            directions.removeIf(blockedDirections::contains);

            LinkedList<Direction> finalDirections = removeDuplicates(directions);
            try {

                room = Room.getLoadedRooms().stream().filter(possibleRoom -> new HashSet<>(possibleRoom.getDirections()).containsAll(finalDirections) && possibleRoom.getDirections().stream().noneMatch(blockedDirections::contains)).findAny().get();
                toReplace.put(vector, room);
            }catch (Exception e) {
                System.out.println(TextUtil.getColored("&6Could not find room with directions: " + finalDirections));
            }
        }
        toReplace.forEach(dungeon::replace);
    }

    public static <T> LinkedList<T> removeDuplicates(LinkedList<T> list) {

        // Create a new ArrayList
        LinkedList<T> newList = new LinkedList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }
}
