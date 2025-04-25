package dev.pingui.kombo.combo;

public enum InputResult {

    INPUT_INVALID,
    INPUT_INVALID_STRICT,
    INPUT_TOO_EARLY,
    INPUT_VALID_NEXT,
    INPUT_VALID_COMPLETED;

    public boolean isCompleted() {
        return this == INPUT_VALID_COMPLETED;
    }
}