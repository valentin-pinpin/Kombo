package dev.pingui.kombo.listener;

import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.input.InputState;
import dev.pingui.kombo.input.InputType;
import dev.pingui.kombo.manager.PlayerInputManager;
import org.bukkit.Input;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("experimental")
public class PlayerInputListener implements Listener {

    private final PlayerInputManager playerInputManager;
    private final Map<UUID, Input> previousInputs;

    public PlayerInputListener(PlayerInputManager playerInputManager) {
        this.playerInputManager = Objects.requireNonNull(playerInputManager, "PlayerInputManager cannot be null");
        this.previousInputs = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onPlayerInput(PlayerInputEvent event) {
        Player player = event.getPlayer();

        Input current = event.getInput();
        Input previous = previousInputs.get(player.getUniqueId());

        if (previous != null) {
            for (PlayerInput input : getChangedInputs(current, previous)) {
                playerInputManager.handlePlayerInput(player, input);
            }
        }

        previousInputs.put(player.getUniqueId(), current);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        previousInputs.remove(event.getPlayer().getUniqueId());
    }

    private static List<PlayerInput> getChangedInputs(Input current, Input previous) {
        List<PlayerInput> inputs = new ArrayList<>();

        for (InputType type : InputType.values()) {
            boolean currentState = type.isActive(current);
            boolean previousState = type.isActive(previous);

            if (currentState != previousState) {
                inputs.add(new PlayerInput(type, InputState.of(currentState)));
            }
        }

        return inputs;
    }
}
