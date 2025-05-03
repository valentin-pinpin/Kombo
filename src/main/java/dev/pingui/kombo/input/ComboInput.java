package dev.pingui.kombo.input;

import java.util.Objects;

public record ComboInput(InputType type, InputState state, long minDelay, long maxDelay) implements Input {

    public ComboInput {
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(state, "State cannot be null");
        if (minDelay < 0) {
            throw new IllegalArgumentException("Min delay cannot be negative");
        }
        if (minDelay > maxDelay) {
            throw new IllegalArgumentException("Min delay cannot exceed max delay");
        }
    }
}
