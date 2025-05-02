package dev.pingui.kombo.manager;

import dev.pingui.kombo.combo.ComboPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ComboPlayerManager {

    private final Map<UUID, ComboPlayer> players = new ConcurrentHashMap<>();

    public ComboPlayer get(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), ComboPlayer::new);
    }

    public void remove(Player player) {
        players.remove(player.getUniqueId());
    }
}
