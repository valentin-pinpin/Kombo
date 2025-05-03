package dev.pingui.kombo;

import dev.pingui.kombo.listener.PlayerInputListener;
import dev.pingui.kombo.manager.ComboPlayerManager;
import dev.pingui.kombo.manager.PlayerInputManager;
import dev.pingui.kombo.manager.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Kombo extends JavaPlugin {

    private static Kombo instance;

    private SkillManager skillManager;
    private ComboPlayerManager playerManager;
    private PlayerInputManager playerInputManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.skillManager = new SkillManager();
        this.playerManager = new ComboPlayerManager();
        this.playerInputManager = new PlayerInputManager(this, skillManager, playerManager);

        Bukkit.getPluginManager().registerEvents(new PlayerInputListener(playerInputManager), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static Kombo getInstance() {
        return instance;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public ComboPlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlayerInputManager getPlayerInputManager() {
        return playerInputManager;
    }
}