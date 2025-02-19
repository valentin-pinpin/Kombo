package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;

import java.util.Objects;

public abstract class AbstractSkill implements Skill {

    private final String id;
    private final Combo combo;

    public AbstractSkill(String id, Combo combo) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.combo = Objects.requireNonNull(combo, "DefaultCombo cannot be null");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Combo combo() {
        return combo;
    }
}
