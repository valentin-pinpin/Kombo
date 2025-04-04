package dev.pingui.kombo.action;

import dev.pingui.kombo.util.Direction;

import java.util.Objects;

public enum ActionType {

    JUMP("key.jump"),
    SNEAK("key.sneak"),
    SPRINT("key.sprint"),
    MOVE_FORWARD("key.forward"),
    MOVE_LEFT("key.left"),
    MOVE_BACKWARD("key.back"),
    MOVE_RIGHT("key.right"),
    UNKNOWN("key.none");

    private final String keybind;

    ActionType(String keybind) {
        this.keybind = keybind;
    }

    public String keybind() {
        return keybind;
    }

    public Direction direction() {
        return switch (this) {
            case MOVE_FORWARD -> Direction.FORWARD;
            case MOVE_BACKWARD -> Direction.BACKWARD;
            case MOVE_LEFT -> Direction.LEFT;
            case MOVE_RIGHT -> Direction.RIGHT;
            default -> Direction.UNKNOWN;
        };
    }

    public static ActionType fromDirection(Direction direction) {
        Objects.requireNonNull(direction, "Direction cannot be null");
        return switch (direction) {
            case FORWARD -> MOVE_FORWARD;
            case BACKWARD -> MOVE_BACKWARD;
            case LEFT -> MOVE_LEFT;
            case RIGHT -> MOVE_RIGHT;
            default -> UNKNOWN;
        };
    }
}