package dev.pingui.kombo.combo;

import dev.pingui.kombo.input.ComboInput;
import dev.pingui.kombo.input.PlayerInput;

import java.util.List;

public class ComboSession {

    private final Combo combo;
    private int index;
    private PlayerInput lastInput;

    public ComboSession(Combo combo) {
        this.combo = combo;
        this.index = 0;
        this.lastInput = PlayerInput.EMPTY;
    }

    public Combo getCombo() {
        return combo;
    }

    public int getIndex() {
        return index;
    }

    public PlayerInput getLastInput() {
        return lastInput;
    }

    private ComboResult processInput(PlayerInput input) {
        List<ComboInput> inputs = combo.inputs();

        if (index >= inputs.size()) {
            return ComboResult.MISMATCH_STRICT;
        }

        ComboInput expected = inputs.get(index);
        long elapsed = input.timestamp() - lastInput.timestamp();

        if (elapsed > expected.maxDelay()) {
            return ComboResult.EXPIRED;
        }

        if (!expected.matches(input)) {
            return combo.strict() ? ComboResult.MISMATCH_STRICT : ComboResult.INVALID_INPUT;
        }

        if (!lastInput.isEmpty()
                && elapsed < expected.minDelay()) {
            return ComboResult.PREMATURE;
        }

        return index + 1 >= inputs.size() ? ComboResult.COMPLETED : ComboResult.ADVANCED;
    }

    public ComboResult applyInput(PlayerInput input) {
        ComboResult result = processInput(input);

        switch (result) {
            case ADVANCED -> {
                index++;
                lastInput = input;
            }
            case EXPIRED, MISMATCH_STRICT, COMPLETED -> reset();
        }

        return result;
    }

    public void reset() {
        index = 0;
        lastInput = PlayerInput.EMPTY;
    }
}
