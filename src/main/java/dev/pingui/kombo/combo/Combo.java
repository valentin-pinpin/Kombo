package dev.pingui.kombo.combo;

import dev.pingui.kombo.input.Input;
import dev.pingui.kombo.input.InputState;
import dev.pingui.kombo.input.InputType;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Combo {

    private final List<Input> inputs;
    private final long minInputDelay;
    private final long maxInputDelay;
    private final boolean strict;

    private Combo(Builder builder) {
        this.inputs = List.copyOf(builder.inputs);
        this.minInputDelay = builder.minInputDelay;
        this.maxInputDelay = builder.maxInputDelay;
        this.strict = builder.strict;
    }

    public long minInputDelay() {
        return minInputDelay;
    }

    public long maxInputDelay() {
        return maxInputDelay;
    }

    public long minComboDuration() {
        return minInputDelay * inputs.size();
    }

    public long maxComboDuration() {
        return maxInputDelay * inputs.size();
    }

    public boolean strict() {
        return strict;
    }

    public List<Input> inputs() {
        return inputs;
    }

    public static class Builder {

        private final List<Input> inputs;
        private long minInputDelay;
        private long maxInputDelay;
        private boolean strict;

        public Builder() {
            this.inputs = new ArrayList<>();
            this.minInputDelay = 0L;
            this.maxInputDelay = Long.MAX_VALUE;
            this.strict = false;
        }

        public Builder input(InputType inputType) {
            return input(inputType, InputState.PRESSED)
                    .input(inputType, InputState.RELEASED);
        }

        public Builder input(InputType inputType, InputState inputState) {
            Objects.requireNonNull(inputType, "InputType cannot be null");
            Objects.requireNonNull(inputState, "InputState cannot be null");
            return input(new Input(inputType, inputState));
        }

        public Builder input(Input input) {
            Objects.requireNonNull(input, "Input cannot be null");
            inputs.add(input);
            return this;
        }

        public Builder minInputDelay(Duration delay) {
            Objects.requireNonNull(delay, "MinInputDelay cannot be null");
            return minInputDelay(delay.toMillis());
        }

        public Builder minInputDelay(long delay, TimeUnit unit) {
            Objects.requireNonNull(unit, "TimeUnit cannot be null");
            return minInputDelay(unit.toMillis(delay));
        }

        public Builder minInputDelay(long delay) {
            if (delay < 0) {
                throw new IllegalArgumentException("MinInputDelay cannot be negative");
            }
            if (delay >= maxInputDelay) {
                throw new IllegalArgumentException("MinInputDelay cannot exceed MaxInputDelay");
            }
            this.minInputDelay = delay;
            return this;
        }

        public Builder maxInputDelay(Duration delay) {
            Objects.requireNonNull(delay, "MaxInputDelay cannot be null");
            return maxInputDelay(delay.toMillis());
        }

        public Builder maxInputDelay(long delay, TimeUnit unit) {
            Objects.requireNonNull(unit, "TimeUnit cannot be null");
            return maxInputDelay(unit.toMillis(delay));
        }

        public Builder maxInputDelay(long delay) {
            if (delay < minInputDelay) {
                throw new IllegalArgumentException("MaxInputDelay cannot be less than MinInputDelay");
            }
            this.maxInputDelay = delay;
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