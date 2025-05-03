package dev.pingui.kombo.input;

public record PlayerInput(InputType type, InputState state, long timestamp) implements Input {

    public static final PlayerInput EMPTY = new PlayerInput(InputType.NONE, InputState.NONE, 0L);

    public PlayerInput(InputType type, InputState state) {
        this(type, state, System.currentTimeMillis());
    }
}
