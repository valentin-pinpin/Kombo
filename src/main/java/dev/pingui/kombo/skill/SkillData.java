package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;

import java.util.Objects;

public record SkillData(String id, String description, String permission, Combo combo, int priority) {

    public SkillData {
        Objects.requireNonNull(id, "Id cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(combo, "Combo cannot be null");
    }

    public SkillData(String id, String description, String permission, Combo combo) {
        this(id, description, permission, combo, 0);
    }
}
