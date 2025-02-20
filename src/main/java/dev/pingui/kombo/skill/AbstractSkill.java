package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;

import java.util.Objects;

public abstract class AbstractSkill implements Skill {

    private final String id;
    private final String permission;
    private final Combo combo;

    public AbstractSkill(String id, String permission, Combo combo) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
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

    @Override
    public String permission() {
        return permission;
    }
}
