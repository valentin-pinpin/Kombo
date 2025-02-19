package dev.pingui.kombo.skill;

import dev.pingui.kombo.combo.Combo;
import org.bukkit.entity.Player;

public interface Skill {

    String id();

    Combo combo();

    String permission();

    default boolean canExecute(Player player) {
        return true;
    }

    void execute(Player player);
}
