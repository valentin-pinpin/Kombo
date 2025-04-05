package dev.pingui.kombo.manager;

import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public class PlayerInputManager {

    private final Plugin plugin;
    private final SkillManager skillManager;
    private final ComboProgressManager comboProgressManager;

    public PlayerInputManager(Plugin plugin, SkillManager skillManager, ComboProgressManager comboProgressManager) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.skillManager = Objects.requireNonNull(skillManager, "SkillManager cannot be null");
        this.comboProgressManager = Objects.requireNonNull(comboProgressManager, "ComboProgressManager cannot be null");
    }

    public void handlePlayerInput(Player player, PlayerInput input) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");

        skillManager.skills().stream()
                .filter(skill -> player.hasPermission(skill.data().permission()))
                .filter(skill -> comboProgressManager.updateProgress(player, skill, input))
                .filter(skill -> skill.canPerform(player))
                .sorted(Comparator.comparingInt(skill -> skill.data().priority()))
                .forEach(skill -> executeSkill(player, skill));
    }

    private void executeSkill(Player player, Skill skill) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                skill.perform(player);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing skill " + skill.data().id() + " for player " + player.getName(), e);
            }
        });
    }
}