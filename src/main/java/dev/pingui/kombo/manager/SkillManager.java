package dev.pingui.kombo.manager;

import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.skill.SkillData;
import dev.pingui.kombo.skill.SkillEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkillManager {

    private final Map<String, SkillEntry> skills;

    public SkillManager() {
        this.skills = new ConcurrentHashMap<>();
    }

    public SkillEntry getSkillEntry(String id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return Objects.requireNonNull(skills.get(id), "Skill with id " + id + " does not exist");
    }

    public void addSkill(SkillEntry skillEntry) {
        Objects.requireNonNull(skillEntry, "SkillEntry cannot be null");
        String skillId = skillEntry.data().id();
        if (skills.containsKey(skillId)) {
            throw new IllegalStateException("Skill with id " + skillId + " already exists");
        }
        skills.put(skillId, skillEntry);
    }

    public void addSkill(SkillData data, Skill skill) {
        addSkill(new SkillEntry(data, skill));
    }

    public Collection<SkillEntry> skills() {
        return List.copyOf(skills.values());
    }
}