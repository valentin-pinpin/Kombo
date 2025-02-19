package dev.pingui.kombo.action;

public enum ActionState {

    STARTED,
    STOPPED;

    public static ActionState of(boolean started) {
        return started ? STARTED : STOPPED;
    }
}