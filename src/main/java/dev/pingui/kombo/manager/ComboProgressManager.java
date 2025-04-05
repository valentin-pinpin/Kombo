package dev.pingui.kombo.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pingui.kombo.combo.Combo;
import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.skill.SkillData;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ComboProgressManager {

    private final Map<String, Cache<UUID, ComboStep>> skillComboCaches;

    public ComboProgressManager() {
        this.skillComboCaches = new ConcurrentHashMap<>();
    }

    public boolean updateProgress(Player player, Skill skill, PlayerInput input) {
        UUID playerId = player.getUniqueId();
        SkillData data = skill.data();
        Combo combo = data.combo();
        Cache<UUID, ComboStep> cache = getOrCreateCache(data.id(), combo.maxInputDelay());

        ComboStep step = cache.getIfPresent(playerId);
        if (step == null) {
            step = ComboStep.ZERO;
        }

        InputResult result = resolveComboStepResult(input, combo, step);
        applyResult(cache, playerId, result, step);

        return result.isCompleted();
    }

    private Cache<UUID, ComboStep> getOrCreateCache(String skillId, long maxDelay) {
        Duration expiration = Duration.ofMillis(maxDelay);
        return skillComboCaches.computeIfAbsent(skillId, id -> CacheBuilder.newBuilder()
                .expireAfterWrite(expiration)
                .build());
    }

    private InputResult resolveComboStepResult(PlayerInput input, Combo combo, ComboStep step) {
        List<PlayerInput> inputs = combo.inputs();

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

    private void applyResult(Cache<UUID, ComboStep> cache, UUID playerId, InputResult result, ComboStep step) {
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
