package dev.pingui.kombo.util;

public enum Direction {

    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UNKNOWN;

    public Direction oppositeDirection() {
        return switch (this) {
            case FORWARD -> BACKWARD;
            case BACKWARD -> FORWARD;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> UNKNOWN;
        };
    }
}
