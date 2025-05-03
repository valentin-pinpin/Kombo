package dev.pingui.kombo.combo;

import dev.pingui.kombo.input.ComboInput;
import dev.pingui.kombo.input.PlayerInput;

import java.util.List;

public class ComboSession {

    private final Combo combo;
    private int index;
    private PlayerInput lastValidInput;

    public ComboSession(Combo combo) {
        this.combo = combo;
        this.index = 0;
        this.lastValidInput = PlayerInput.EMPTY;
    }

    public Combo getCombo() {
        return combo;
    }

    public int getIndex() {
        return index;
    }

    public PlayerInput getLastValidInput() {
        return lastValidInput;
    }

    private ComboResult processInput(PlayerInput input) {
        List<ComboInput> inputs = combo.inputs();

        if (index >= inputs.size()) {
            return ComboResult.MISMATCH_STRICT;
        }

        ComboInput expected = inputs.get(index);
        boolean hasPrevious = !lastValidInput.isEmpty();
        boolean matches = expected.matches(input);

        if (!matches && combo.strict()) {
            return ComboResult.MISMATCH_STRICT;
        }

        if (hasPrevious) {
            long elapsed = input.timestamp() - lastValidInput.timestamp();

            if (elapsed > expected.maxDelay()) {
                return ComboResult.EXPIRED;
            }
            if (elapsed < expected.minDelay()) {
                return ComboResult.PREMATURE;
            }
        }

        if (!matches) {
            return ComboResult.INVALID_INPUT;
        }

        return index + 1 >= inputs.size() ? ComboResult.COMPLETED : ComboResult.ADVANCED;
    }

    public ComboResult applyInput(PlayerInput input) {
        ComboResult result = processInput(input);

        switch (result) {
            case ADVANCED -> {
                index++;
                lastValidInput = input;
            }
            case EXPIRED, MISMATCH_STRICT, COMPLETED -> {
                index = 0;
                lastValidInput = PlayerInput.EMPTY;
            }
        }

        return result;
    }
}
