package dev.pingui.kombo.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;

public class MovementUtils {

    public static Direction getMovementDirection(Location from, Location to) {
        Objects.requireNonNull(from, "From cannot be null");
        Objects.requireNonNull(to, "To cannot be null");

        if (from.equals(to)) {
            return Direction.UNKNOWN;
        }

        Vector movementDirection = to.toVector().subtract(from.toVector()).setY(0).normalize();
        Vector facingDirection = from.getDirection().setY(0).normalize();

        double dot = movementDirection.dot(facingDirection);
        double crossProductZ = facingDirection.crossProduct(movementDirection).getY();

        if (Math.abs(dot) > 0.5) {
            return dot > 0 ? Direction.FORWARD : Direction.BACKWARD;
        }

        if (Math.abs(crossProductZ) > 0.5) {
            return crossProductZ > 0 ? Direction.LEFT : Direction.RIGHT;
        }

        return Direction.UNKNOWN;
    }
}
