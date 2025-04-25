package dev.pingui.kombo.input;

public interface Input {

    InputType type();

    InputState state();

    default boolean matches(Input input) {
        return this.type() == input.type() && this.state() == input.state();
    }
}
