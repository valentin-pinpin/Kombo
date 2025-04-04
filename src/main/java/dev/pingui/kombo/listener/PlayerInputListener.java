package dev.pingui.kombo.listener;

import dev.pingui.kombo.input.Input;
import dev.pingui.kombo.input.InputState;
import dev.pingui.kombo.input.InputType;
import dev.pingui.kombo.manager.PlayerInputManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("experimental")
public class PlayerInputListener implements Listener {

    private final PlayerInputManager actionManager;
    private final Map<UUID, org.bukkit.Input> previousInputs;

    public PlayerInputListener(PlayerInputManager actionManager) {
        this.actionManager = Objects.requireNonNull(actionManager, "ActionManager cannot be null");
        this.previousInputs = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onPlayerInput(PlayerInputEvent event) {
        Player player = event.getPlayer();

        org.bukkit.Input current = event.getInput();
        org.bukkit.Input previous = previousInputs.get(player.getUniqueId());

        if (previous != null) {
            compare(current, previous).forEach(input -> actionManager.handlePlayerInput(player, input));
        }

        previousInputs.put(player.getUniqueId(), current);
    }

    private static List<Input> compare(org.bukkit.Input current, org.bukkit.Input previous) {
        List<Input> inputs = new ArrayList<>();

        for (InputType type : InputType.values()) {
            boolean currentState = type.get(current);
            boolean previousState = type.get(previous);

            if (currentState != previousState) {
                inputs.add(new Input(type, InputState.of(currentState)));
            }
        }

        return inputs;
    }
}
