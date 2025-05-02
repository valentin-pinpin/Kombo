package dev.pingui.kombo.api;

import dev.pingui.kombo.Kombo;
import dev.pingui.kombo.skill.Skill;

public final class KomboAPI {

    public static void registerSkill(Skill skill) {
        Kombo.getInstance().getSkillManager().addSkill(skill);
    }
}
