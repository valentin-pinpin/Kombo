package dev.pingui.kombo.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import dev.pingui.kombo.KomboPlugin;
import dev.pingui.kombo.action.Action;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.action.ActionType;
import dev.pingui.kombo.manager.PlayerActionManager;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ActionPacketHandler {

    private final PlayerActionManager actionManager;

    public ActionPacketHandler(PlayerActionManager actionManager) {
        this.actionManager = Objects.requireNonNull(actionManager, "ActionManager cannot be null");
    }

    public PacketAdapter getPacketAdapter() {
        return new PacketAdapter(KomboPlugin.inst(), PacketType.Play.Client.ENTITY_ACTION) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                EnumWrappers.PlayerAction playerAction = packet.getPlayerActions().read(0);

                Action action = mapPlayerActionToAction(playerAction);
                if (action != null) {
                    actionManager.playerAction(player, action);
                }
            }
        };
    }

    private Action mapPlayerActionToAction(EnumWrappers.PlayerAction playerAction) {
        return switch (playerAction) {
            case START_SNEAKING -> new Action(ActionType.SNEAK, ActionState.STARTED);
            case STOP_SNEAKING -> new Action(ActionType.SNEAK, ActionState.STOPPED);
            case START_SPRINTING -> new Action(ActionType.SPRINT, ActionState.STARTED);
            case STOP_SPRINTING -> new Action(ActionType.SPRINT, ActionState.STOPPED);
            default -> null;
        };
    }
}