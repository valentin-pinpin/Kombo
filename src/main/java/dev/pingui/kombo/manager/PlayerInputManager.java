package dev.pingui.kombo.manager;

import dev.pingui.kombo.combo.ComboPlayer;
import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.skill.SkillData;
import dev.pingui.kombo.skill.SkillEntry;
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
                .filter(skillEntry -> hasPermission(player, skillEntry.data()))
                .filter(skillEntry -> comboCompleted(comboPlayer, skillEntry.data(), input))
                .filter(skillEntry -> canPerform(player, skillEntry.skill()))
                .max(Comparator.comparingInt(skillEntry -> skillEntry.data().priority()))
                .ifPresent(skillEntry -> executeSkill(player, skillEntry));
    }

    public void executeSkill(Player player, SkillEntry skillEntry) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                skillEntry.skill().perform(player);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing skill " + skillEntry.data().id() + " for player " + player.getName(), e);
            }
        });
    }

    public boolean hasPermission(Player player, SkillData data) {
        return player.hasPermission(data.permission());
    }

    public boolean comboCompleted(ComboPlayer comboPlayer, SkillData data, PlayerInput input) {
        return comboPlayer.applyInput(data, input).isCompleted();
    }

    public boolean canPerform(Player player, Skill skill) {
        return skill.canPerform(player);
    }
}