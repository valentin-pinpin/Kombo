package dev.pingui.kombo.combo;

public record ComboStep(int index, long executedAt) {

    public static final ComboStep ZERO = new ComboStep(0, 0L);

    public ComboStep next() {
        return new ComboStep(index + 1, System.currentTimeMillis());
    }
}