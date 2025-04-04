package dev.pingui.kombo.input;

public enum InputState {

    PRESSED,
    HELD,
    RELEASED;

    public boolean isPressed() {
        return this == PRESSED;
    }

    public boolean isHeld() {
        return this == HELD;
    }

    public boolean isReleased() {
        return this == RELEASED;
    }

    public static InputState of(boolean pressed) {
        return pressed ? PRESSED : RELEASED;
    }
}