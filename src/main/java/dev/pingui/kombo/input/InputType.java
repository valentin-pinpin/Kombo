package dev.pingui.kombo.input;

import org.bukkit.Input;

import java.util.function.Function;

public enum InputType {

    FORWARD("key.forward", Input::isForward),
    BACKWARD("key.back", Input::isBackward),
    LEFT("key.left", Input::isLeft),
    RIGHT("key.right", Input::isRight),
    JUMP("key.jump", Input::isJump),
    SNEAK("key.sneak", Input::isSneak),
    SPRINT("key.sprint", Input::isSprint),
    NONE("key.none");

    private final String keybind;
    private final Function<Input, Boolean> function;

    InputType(String keybind) {
        this(keybind, input -> false);
    }

    InputType(String keybind, Function<Input, Boolean> function) {
        this.keybind = keybind;
        this.function = function;
    }

    public String keybind() {
        return keybind;
    }

    public boolean isActive(Input input) {
        return function.apply(input);
    }
}