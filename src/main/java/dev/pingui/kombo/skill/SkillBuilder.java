package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SkillBuilder {

    private final String id;
    private final Combo combo;
    private String description;
    private String permission;
    private Predicate<Player> predicate;
    private Consumer<Player> consumer;

    public SkillBuilder(String id, Combo combo) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.combo = Objects.requireNonNull(combo, "Combo cannot be null");
        this.description = "";
        this.permission = "";
        this.predicate = player -> true;
        this.consumer = player -> {};
    }

    public SkillBuilder description(String description) {
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        return this;
    }

    public SkillBuilder permission(String permission) {
        this.permission = Objects.requireNonNull(permission, "Permission cannot be null");
        return this;
    }

    public SkillBuilder predicate(Predicate<Player> predicate) {
        this.predicate = Objects.requireNonNull(predicate, "Predicate cannot be null");
        return this;
    }

    public SkillBuilder consumer(Consumer<Player> consumer) {
        this.consumer = Objects.requireNonNull(consumer, "Consumer cannot be null");
        return this;
    }

    public Skill build() {
        SkillData data = new SkillData(id, description, permission, combo);
        return new AbstractSkill(data) {

            @Override
            public boolean canPerform(Player player) {
                return predicate.test(player);
            }

            @Override
            public void perform(Player player) {
                consumer.accept(player);
            }
        };
    }
}
