package dev.pingui.kombo.api;

import dev.pingui.kombo.Kombo;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.skill.SkillData;
import dev.pingui.kombo.skill.SkillEntry;

public final class KomboAPI {

    public static void registerSkill(SkillData data, Skill skill) {
        Kombo.getInstance().getSkillManager().addSkill(data, skill);
    }

    public static void registerSkill(SkillEntry skillEntry) {
        Kombo.getInstance().getSkillManager().addSkill(skillEntry);
    }
}
