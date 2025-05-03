package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;

import java.util.Objects;

public class SkillDataBuilder {

    private final String id;
    private final Combo.Builder comboBuilder = new Combo.Builder();
    private String description = "";
    private String permission = "";
    private int priority = 0;

    public SkillDataBuilder(String id) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
    }

    public Combo.Builder combo() {
        return comboBuilder;
    }

    public SkillDataBuilder description(String description) {
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        return this;
    }

    public SkillDataBuilder permission(String permission) {
        this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
        return this;
    }

    public SkillDataBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public SkillData build() {
        Combo combo = comboBuilder.build();
        return new SkillData(id, description, permission, combo, priority);
    }
}
