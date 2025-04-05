package dev.pingui.kombo.api;

import dev.pingui.kombo.Kombo;
import dev.pingui.kombo.skill.Skill;

public final class KomboAPIImpl implements KomboAPI {

    private final Kombo plugin;

    public KomboAPIImpl(Kombo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerSkill(Skill skill) {
        plugin.getSkillManager().addSkill(skill);
    }
}
