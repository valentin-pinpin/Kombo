package dev.pingui.kombo.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.manager.PlayerActionManager;
import dev.pingui.kombo.KomboPlugin;
import dev.pingui.kombo.action.ActionType;
import dev.pingui.kombo.util.Direction;
import dev.pingui.kombo.util.MovementUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class MovementPacketHandler implements Listener {

    private final Map<Player, Map<Direction, BukkitTask>> tasks;
    private final PlayerActionManager actionManager;

    public MovementPacketHandler(PlayerActionManager actionManager) {
        this.actionManager = Objects.requireNonNull(actionManager, "ActionManager cannot be null");
        this.tasks = new ConcurrentHashMap<>();
    }

    public PacketAdapter getPacketAdapter() {
        return new PacketAdapter(KomboPlugin.inst(), PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                double x = packet.getDoubles().read(0);
                double y = packet.getDoubles().read(1);
                double z = packet.getDoubles().read(2);

                Location from = player.getLocation();
                Location to = new Vector(x, y, z).toLocation(from.getWorld());

                if (from.distance(to) > 0.08) {
                    Direction direction = MovementUtils.getMovementDirection(from, to);

                    if (direction != null) {
                        updateActionState(player, direction);
                    }
                }
            }
        };
    }

    private void updateActionState(Player player, Direction direction) {
        Map<Direction, BukkitTask> playerTasks = tasks.computeIfAbsent(player, k -> new ConcurrentHashMap<>());
        ActionType type = ActionType.fromDirection(direction);

        if (!playerTasks.containsKey(direction)) {
            actionManager.playerAction(player, type, ActionState.STARTED);
        } else {
            playerTasks.get(direction).cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(KomboPlugin.inst(), () -> {
            actionManager.playerAction(player, type, ActionState.STOPPED);
            playerTasks.remove(direction);
        }, 2);

        playerTasks.put(direction, task);

        Direction opposite = direction.opposite();
        BukkitTask oppositeTask = playerTasks.remove(opposite);
        if (oppositeTask != null) {
            oppositeTask.cancel();
            actionManager.playerAction(player, ActionType.fromDirection(opposite), ActionState.STOPPED);
        }
    }

    public void clearPlayerCache(Player player) {
        Map<Direction, BukkitTask> playerTasks = tasks.remove(player);
        if (playerTasks != null) {
            playerTasks.values().forEach(BukkitTask::cancel);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearPlayerCache(event.getPlayer());
    }
}