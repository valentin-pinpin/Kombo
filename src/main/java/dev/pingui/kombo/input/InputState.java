package dev.pingui.kombo.input;

public enum InputState {

    PRESSED,
    HELD,
    RELEASED,
    NONE;

    public static InputState of(boolean pressed) {
        return pressed ? PRESSED : RELEASED;
    }
}