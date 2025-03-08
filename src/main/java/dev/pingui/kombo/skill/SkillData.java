package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;

import java.util.Objects;

public record SkillData(String id, String description, String permission, Combo combo) {

    public SkillData {
        Objects.requireNonNull(id, "Id cannot be null");
        Objects.requireNonNull(description, "Description cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(combo, "Combo cannot be null");
    }
}
