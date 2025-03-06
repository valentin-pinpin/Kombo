package dev.pingui.kombo.manager;

import dev.pingui.kombo.skill.Skill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkillManager {

    private final Map<String, Skill> skills;

    public SkillManager() {
        this.skills = new ConcurrentHashMap<>();
    }

    public Skill getSkill(String id) {
        Objects.requireNonNull(id, "Id cannot be null");
        return Objects.requireNonNull(skills.get(id), "Skill with id " + id + " does not exist");
    }

    public void addSkill(Skill skill) {
        Objects.requireNonNull(skill, "Skill cannot be null");
        skills.putIfAbsent(skill.id(), skill);
    }

    public Collection<Skill> skills() {
        return List.copyOf(skills.values());
    }
}