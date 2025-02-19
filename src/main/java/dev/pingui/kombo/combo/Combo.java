package dev.pingui.kombo.combo;

import dev.pingui.kombo.action.Action;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.action.ActionType;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Combo {

    private final List<Action> actions;
    private final long minActionDelay;
    private final long maxActionDelay;
    private final boolean strict;

    private Combo(Builder builder) {
        this.actions = List.copyOf(builder.actions);
        this.minActionDelay = builder.minActionDelay;
        this.maxActionDelay = builder.maxActionDelay;
        this.strict = builder.strict;
    }

    public long minActionDelay() {
        return minActionDelay;
    }

    public long maxActionDelay() {
        return maxActionDelay;
    }

    public long minSequenceDuration() {
        return minActionDelay * actions.size();
    }

    public long maxSequenceDuration() {
        return maxActionDelay * actions.size();
    }

    public boolean strict() {
        return strict;
    }

    public List<Action> actions() {
        return actions;
    }

    public static class Builder {

        private final List<Action> actions;
        private long minActionDelay;
        private long maxActionDelay;
        private boolean strict;

        public Builder() {
            this.actions = new ArrayList<>();
            this.minActionDelay = 0L;
            this.maxActionDelay = Long.MAX_VALUE;
            this.strict = false;
        }

        public Builder appendAction(ActionType actionType) {
            return appendAction(actionType, ActionState.STARTED)
                    .appendAction(actionType, ActionState.STOPPED);
        }

        public Builder appendAction(ActionType actionType, ActionState actionState) {
            Objects.requireNonNull(actionType, "ActionType cannot be null");
            Objects.requireNonNull(actionState, "ActionState cannot be null");
            return appendAction(new Action(actionType, actionState));
        }

        public Builder appendAction(Action action) {
            Objects.requireNonNull(action, "Action cannot be null");
            actions.add(action);
            return this;
        }

        public Builder minActionDelay(Duration delay) {
            Objects.requireNonNull(delay, "MinActionDelay cannot be null");
            return minActionDelay(delay.toMillis());
        }

        public Builder minActionDelay(long delay, TimeUnit unit) {
            Objects.requireNonNull(unit, "TimeUnit cannot be null");
            return minActionDelay(unit.toMillis(delay));
        }

        public Builder minActionDelay(long delay) {
            if (delay < 0) {
                throw new IllegalArgumentException("MinActionDelay cannot be negative");
            }
            if (delay >= maxActionDelay) {
                throw new IllegalArgumentException("MinActionDelay cannot exceed MaximumActionDelay");
            }
            this.minActionDelay = delay;
            return this;
        }

        public Builder maxActionDelay(Duration delay) {
            Objects.requireNonNull(delay, "MaxActionDelay cannot be null");
            return maxActionDelay(delay.toMillis());
        }

        public Builder maxActionDelay(long delay, TimeUnit unit) {
            Objects.requireNonNull(unit, "TimeUnit cannot be null");
            return maxActionDelay(unit.toMillis(delay));
        }

        public Builder maxActionDelay(long delay) {
            if (delay < minActionDelay) {
                throw new IllegalArgumentException("MaxActionDelay cannot be less than MinActionDelay");
            }
            this.maxActionDelay = delay;
            return this;
        }

        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        public Combo build() {
            if (actions.isEmpty()) {
                throw new IllegalArgumentException("ActionSequence cannot be empty");
            }
            return new Combo(this);
        }
    }
}