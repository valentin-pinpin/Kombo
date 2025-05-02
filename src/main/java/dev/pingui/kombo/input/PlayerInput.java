package dev.pingui.kombo.input;

public record PlayerInput(InputType type, InputState state, long timestamp) implements Input {

    public PlayerInput(InputType type, InputState state) {
        this(type, state, System.currentTimeMillis());
    }
}
