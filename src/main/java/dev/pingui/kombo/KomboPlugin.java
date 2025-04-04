package dev.pingui.kombo;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.pingui.kombo.manager.PlayerActionManager;
import dev.pingui.kombo.listener.MovementPacketHandler;
import dev.pingui.kombo.listener.PlayerJumpListener;
import dev.pingui.kombo.listener.ActionPacketHandler;
import dev.pingui.kombo.manager.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KomboPlugin extends JavaPlugin {

    private static KomboPlugin instance;

    private PlayerActionManager playerActionManager;
    private SkillManager skillManager;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.skillManager = new SkillManager();
        this.playerActionManager = new PlayerActionManager(this, skillManager);

        Bukkit.getPluginManager().registerEvents(new PlayerJumpListener(playerActionManager), this);

        // ProtocolLib
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        ActionPacketHandler actionPacketHandler = new ActionPacketHandler(playerActionManager);
        protocolManager.addPacketListener(actionPacketHandler.getPacketAdapter());

        MovementPacketHandler movementPacketHandler = new MovementPacketHandler(playerActionManager);
        protocolManager.addPacketListener(movementPacketHandler.getPacketAdapter());
        Bukkit.getPluginManager().registerEvents(movementPacketHandler, this);
    }

    public static KomboPlugin inst() {
        return Objects.requireNonNull(instance, "Kombo has not been initialized yet");
    }

    public PlayerActionManager getPlayerActionManager() {
        return playerActionManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }
}