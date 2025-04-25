package dev.pingui.kombo.input;

public record ComboInput(InputType type, InputState state, long minDelay, long maxDelay) implements Input {

    public ComboInput {
        if (minDelay < 0 || minDelay >= maxDelay) {
            throw new IllegalArgumentException();
        }
    }
}
