package dev.pingui.kombo.manager;

import dev.pingui.kombo.combo.ComboPlayer;
import dev.pingui.kombo.combo.ComboResult;
import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Level;

public final class PlayerInputManager {

    private final Plugin plugin;
    private final SkillManager skillManager;
    private final ComboPlayerManager playerManager;

    public PlayerInputManager(Plugin plugin, SkillManager skillManager, ComboPlayerManager playerManager) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.skillManager = Objects.requireNonNull(skillManager, "SkillManager cannot be null");
        this.playerManager = Objects.requireNonNull(playerManager, "PlayerManager cannot be null");
    }

    public void handlePlayerInput(Player player, PlayerInput input) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");

        ComboPlayer comboPlayer = playerManager.get(player);

        skillManager.skills().stream()
                .filter(skill -> hasPermission(player, skill))
                .filter(skill -> comboCompleted(comboPlayer, skill, input))
                .filter(skill -> canPerform(player, skill))
                .sorted(Comparator.comparingInt(skill -> skill.data().priority()))
                .forEach(skill -> executeSkill(player, skill));
    }

    public void executeSkill(Player player, Skill skill) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                skill.perform(player);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing skill " + skill.data().id() + " for player " + player.getName(), e);
            }
        });
    }

    public boolean hasPermission(Player player, Skill skill) {
        return player.hasPermission(skill.data().permission());
    }

    public boolean comboCompleted(ComboPlayer comboPlayer, Skill skill, PlayerInput input) {
        ComboResult result = comboPlayer.applyInput(skill.data(), input);
        return result.isCompleted();
    }

    public boolean canPerform(Player player, Skill skill) {
        return skill.canPerform(player);
    }
}