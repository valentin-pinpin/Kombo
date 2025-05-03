package dev.pingui.kombo.skill;

import java.util.Objects;

public record SkillEntry(SkillData data, Skill skill) {

    public SkillEntry {
        Objects.requireNonNull(data, "SkillData cannot be null");
        Objects.requireNonNull(skill, "Skill cannot be null");
    }
}
