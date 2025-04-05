package dev.pingui.kombo;

import dev.pingui.kombo.listener.PlayerInputListener;
import dev.pingui.kombo.manager.ComboProgressManager;
import dev.pingui.kombo.manager.PlayerInputManager;
import dev.pingui.kombo.manager.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KomboPlugin extends JavaPlugin {

    private static KomboPlugin instance;

    private SkillManager skillManager;
    private ComboProgressManager comboProgressManager;
    private PlayerInputManager playerInputManager;

    @Override
    public void onEnable() {
        instance = this;

        this.skillManager = new SkillManager();
        this.comboProgressManager = new ComboProgressManager();
        this.playerInputManager = new PlayerInputManager(this, skillManager, comboProgressManager);

        Bukkit.getPluginManager().registerEvents(new PlayerInputListener(playerInputManager), this);
    }

    public static KomboPlugin inst() {
        return Objects.requireNonNull(instance, "Kombo has not been initialized yet");
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public ComboProgressManager getComboProgressManager() {
        return comboProgressManager;
    }

    public PlayerInputManager getPlayerInputManager() {
        return playerInputManager;
    }
}