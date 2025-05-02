package dev.pingui.kombo.combo;

import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.SkillData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboPlayer {

    private final UUID uuid;
    private final Map<String, ComboSession> sessions = new HashMap<>();

    public ComboPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public ComboResult applyInput(SkillData data, PlayerInput input) {
        ComboSession session = sessions.computeIfAbsent(data.id(), id -> new ComboSession(data.combo()));
        return session.applyInput(input);
    }
}
