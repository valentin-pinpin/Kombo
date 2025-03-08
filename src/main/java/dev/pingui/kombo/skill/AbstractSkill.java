package dev.pingui.kombo.skill;

import java.util.Objects;

public abstract class AbstractSkill implements Skill {

    private final SkillData data;

    public AbstractSkill(SkillData data) {
        this.data = Objects.requireNonNull(data, "SkillData cannot be null");
    }

    @Override
    public SkillData data() {
        return data;
    }
}
