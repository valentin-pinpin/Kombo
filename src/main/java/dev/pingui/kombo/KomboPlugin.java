package dev.pingui.kombo;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.action.ActionType;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.manager.PlayerActionManager;
import dev.pingui.kombo.listener.MovementPacketHandler;
import dev.pingui.kombo.listener.PlayerJumpListener;
import dev.pingui.kombo.listener.ActionPacketHandler;
import dev.pingui.kombo.manager.SkillManager;
import dev.pingui.kombo.combo.Combo;
import dev.pingui.kombo.skill.SkillBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.TestOnly;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

        test();
    }

    @TestOnly
    private void test() {
        Skill skill = new SkillBuilder(
                "test_skill",
                new Combo.Builder()
                        .maxActionDelay(700, TimeUnit.MILLISECONDS)
                        .appendAction(ActionType.MOVE_FORWARD, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_FORWARD, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_BACKWARD, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_BACKWARD, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_LEFT, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_RIGHT, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_LEFT, ActionState.STARTED)
                        .appendAction(ActionType.MOVE_RIGHT, ActionState.STARTED)
                        .appendAction(ActionType.SNEAK, ActionState.STARTED)
                        .appendAction(ActionType.JUMP, ActionState.STARTED)
                        .build())
                .permission("kombo.admin")
                .predicate(player -> player.getGameMode() == GameMode.CREATIVE)
                .consumer(player -> getLogger().info("test ok"))
                .build();
        skillManager.addSkill(skill);
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