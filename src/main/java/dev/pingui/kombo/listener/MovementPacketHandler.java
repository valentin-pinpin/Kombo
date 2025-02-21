package dev.pingui.kombo.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.manager.PlayerActionManager;
import dev.pingui.kombo.KomboPlugin;
import dev.pingui.kombo.action.ActionType;
import dev.pingui.kombo.util.Direction;
import dev.pingui.kombo.util.MovementUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class MovementPacketHandler implements Listener {

    private final Map<Player, Cache<Direction, Long>> caches;
    private final PlayerActionManager actionManager;

    public MovementPacketHandler(PlayerActionManager actionManager) {
        this.actionManager = Objects.requireNonNull(actionManager, "ActionManager cannot be null");
        this.caches = new ConcurrentHashMap<>();
    }

    public PacketAdapter getPacketAdapter() {
        return new PacketAdapter(KomboPlugin.inst(), PacketType.Play.Client.POSITION) {

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

    private Cache<Direction, Long> createMovementCache(Player player) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(100, TimeUnit.MILLISECONDS)
                .removalListener(notification -> {
                    ActionType type = ActionType.fromDirection((Direction) notification.getKey());
                    actionManager.playerAction(player, type, ActionState.STOPPED);
                }).build();
    }

    private void updateActionState(Player player, Direction direction) {
        Cache<Direction, Long> cache = caches.computeIfAbsent(player, this::createMovementCache);

        Direction opposite = direction.oppositeDirection();
        Long oppositeTime = cache.getIfPresent(opposite);

        if (oppositeTime != null) {
            cache.invalidate(opposite);
        }

        if (cache.getIfPresent(direction) == null) {
            ActionType type = ActionType.fromDirection(direction);
            actionManager.playerAction(player, type, ActionState.STARTED);
        }

        cache.put(direction, System.currentTimeMillis());
    }

    public void clearPlayerCache(Player player) {
        caches.remove(Objects.requireNonNull(player, "Player cannot be null"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearPlayerCache(event.getPlayer());
    }
}