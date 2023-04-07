package xyz.raitaki.rdungeons.dungeon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Direction {

    UP(0, 1, 0),
    DOWN(0, -1, 0),
    NORTH(0, 0, -1),
    SOUTH(0, 0, 1),
    EAST(1, 0, 0),
    WEST(-1, 0, 0);

    @Getter final int x;
    @Getter final int y;
    @Getter final int z;

    public Direction getOppositeDirection() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case NORTH -> SOUTH;
        };
    }
}