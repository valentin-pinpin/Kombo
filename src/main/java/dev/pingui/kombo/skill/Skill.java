package dev.pingui.kombo.skill;

import org.bukkit.entity.Player;

public interface Skill {

    default boolean canPerform(Player player) {
        return true;
    }

    void perform(Player player);
}
