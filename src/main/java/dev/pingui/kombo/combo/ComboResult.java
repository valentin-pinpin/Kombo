package dev.pingui.kombo.combo;

public enum ComboResult {

    INVALID_INPUT,
    MISMATCH_STRICT,
    PREMATURE,
    EXPIRED,
    ADVANCED,
    COMPLETED;

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}