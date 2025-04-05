package dev.pingui.kombo;

import dev.pingui.kombo.api.KomboAPI;
import dev.pingui.kombo.api.KomboAPIImpl;
import dev.pingui.kombo.listener.PlayerInputListener;
import dev.pingui.kombo.manager.ComboProgressManager;
import dev.pingui.kombo.manager.PlayerInputManager;
import dev.pingui.kombo.manager.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Kombo extends JavaPlugin {

    private static Kombo instance;
    private static KomboAPI api;

    private SkillManager skillManager;
    private ComboProgressManager comboProgressManager;
    private PlayerInputManager playerInputManager;

    @Override
    public void onLoad() {
        instance = this;
        api = new KomboAPIImpl(this);
    }

    @Override
    public void onEnable() {
        this.skillManager = new SkillManager();
        this.comboProgressManager = new ComboProgressManager();
        this.playerInputManager = new PlayerInputManager(this, skillManager, comboProgressManager);

        Bukkit.getPluginManager().registerEvents(new PlayerInputListener(playerInputManager), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static Kombo getInstance() {
        return instance;
    }

    public static KomboAPI getAPI() {
        return api;
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