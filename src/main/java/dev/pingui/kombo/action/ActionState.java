package dev.pingui.kombo.action;

public enum ActionState {

    STARTED,
    STOPPED;

    public boolean started() {
        return this == STARTED;
    }

    public static ActionState of(boolean started) {
        return started ? STARTED : STOPPED;
    }
}