package dev.pingui.kombo.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.manager.PlayerActionManager;
import dev.pingui.kombo.action.ActionType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public final class PlayerJumpListener implements Listener {

    private final PlayerActionManager actionManager;

    public PlayerJumpListener(PlayerActionManager actionManager) {
        this.actionManager = Objects.requireNonNull(actionManager, "ActionManager cannot be null");
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        actionManager.playerAction(event.getPlayer(), ActionType.JUMP, ActionState.STARTED);
    }
}