package dev.pingui.kombo.combo;

import dev.pingui.kombo.input.ComboInput;
import dev.pingui.kombo.input.InputState;
import dev.pingui.kombo.input.InputType;

import java.util.*;

public class Combo {

    private final List<ComboInput> inputs;
    private final long minComboDuration;
    private final long maxComboDuration;
    private final boolean strict;

    private Combo(Builder builder) {
        this.inputs = List.copyOf(builder.inputs);
        this.strict = builder.strict;
        this.minComboDuration = inputs.stream().mapToLong(ComboInput::minDelay).sum();
        this.maxComboDuration = inputs.stream().mapToLong(ComboInput::maxDelay).sum();
    }

    public List<ComboInput> inputs() {
        return inputs;
    }

    public boolean strict() {
        return strict;
    }

    public long minComboDuration() {
        return minComboDuration;
    }

    public long maxComboDuration() {
        return maxComboDuration;
    }

    public static class Builder {

        private final List<ComboInput> inputs;
        private boolean strict;

        public Builder() {
            this.inputs = new ArrayList<>();
            this.strict = false;
        }

        public Builder input(InputType type, InputState state) {
            return input(type, state, 0, Long.MAX_VALUE);
        }

        public Builder input(InputType type, InputState state, long minDelay, long maxDelay) {
            return input(new ComboInput(type, state, minDelay, maxDelay));
        }

        public Builder input(ComboInput input) {
            Objects.requireNonNull(input, "Input cannot be null");
            inputs.add(input);
            return this;
        }

        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        public Combo build() {
            if (inputs.isEmpty()) {
                throw new IllegalArgumentException("Inputs cannot be empty");
            }
            return new Combo(this);
        }
    }
}