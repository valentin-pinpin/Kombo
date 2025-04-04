package dev.pingui.kombo.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pingui.kombo.input.Input;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.combo.Combo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PlayerInputManager {

    private final Plugin plugin;
    private final SkillManager skillManager;
    private final Map<String, Cache<UUID, ComboStep>> cacheSkill;

    public PlayerInputManager(Plugin plugin, SkillManager skillManager) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.skillManager = Objects.requireNonNull(skillManager, "SkillManager cannot be null");
        this.cacheSkill = new ConcurrentHashMap<>();
    }

    public void handlePlayerInput(Player player, Input input) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(input, "Input cannot be null");

        skillManager.skills().stream()
                .filter(skill -> player.hasPermission(skill.data().permission()))
                .filter(skill -> matchComboStep(skill, player, input))
                .filter(skill -> skill.canPerform(player))
                .forEach(skill -> executeSkill(player, skill));
    }

    private void executeSkill(Player player, Skill skill) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                skill.perform(player);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing skill " + skill.data().id() + " for player " + player.getName(), e);
            }
        });
    }

    private Cache<UUID, ComboStep> getOrCreateCache(Skill skill) {
        Duration expiration = Duration.ofMillis(skill.data().combo().maxInputDelay());
        return cacheSkill.computeIfAbsent(skill.data().id(), id -> CacheBuilder.newBuilder()
                .expireAfterWrite(expiration)
                .build());
    }

    private boolean matchComboStep(Skill skill, Player player, Input input) {
        UUID playerId = player.getUniqueId();
        Cache<UUID, ComboStep> cache = getOrCreateCache(skill);
        Combo combo = skill.data().combo();

        ComboStep step = cache.getIfPresent(playerId);
        if (step == null) {
            step = ComboStep.ZERO;
        }

        InputResult result = resolveComboStepResult(combo, step, input);
        applyComboStepResult(cache, playerId, result, step);

        return result.isCompleted();
    }

    private InputResult resolveComboStepResult(Combo combo, ComboStep step, Input input) {
        List<Input> inputs = combo.inputs();

        if (step.index() >= inputs.size()) {
            return InputResult.INVALID_INPUT_STRICT;
        }

        if (!inputs.get(step.index()).equals(input)) {
            return combo.strict() ? InputResult.INVALID_INPUT_STRICT : InputResult.INVALID_INPUT;
        }

        if (step.executedAt() > 0 && (System.currentTimeMillis() - step.executedAt()) < combo.minInputDelay()) {
            return InputResult.TOO_EARLY;
        }

        return (step.index() + 1 >= inputs.size()) ? InputResult.COMPLETED : InputResult.NEXT_STEP;
    }

    private void applyComboStepResult(Cache<UUID, ComboStep> cache, UUID playerId, InputResult result, ComboStep step) {
        switch (result) {
            case INVALID_INPUT_STRICT, COMPLETED -> cache.invalidate(playerId);
            case NEXT_STEP -> cache.put(playerId, step.next());
        }
    }

    private record ComboStep(int index, long executedAt) {

        public static final ComboStep ZERO = new ComboStep(0, 0L);

        public ComboStep next() {
            return new ComboStep(index + 1, System.currentTimeMillis());
        }
    }

    private enum InputResult {

        INVALID_INPUT,
        INVALID_INPUT_STRICT,
        TOO_EARLY,
        NEXT_STEP,
        COMPLETED;

        public boolean isCompleted() {
            return this == COMPLETED;
        }
    }
}