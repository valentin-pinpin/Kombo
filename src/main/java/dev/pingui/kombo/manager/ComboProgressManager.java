package dev.pingui.kombo.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pingui.kombo.combo.Combo;
import dev.pingui.kombo.combo.ComboStep;
import dev.pingui.kombo.combo.InputResult;
import dev.pingui.kombo.input.ComboInput;
import dev.pingui.kombo.input.PlayerInput;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.skill.SkillData;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ComboProgressManager {

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
        List<ComboInput> inputs = combo.inputs();

        if (step.index() >= inputs.size()) {
            return InputResult.INPUT_INVALID_STRICT;
        }

        if (!inputs.get(step.index()).matches(input)) {
            return combo.strict() ? InputResult.INPUT_INVALID_STRICT : InputResult.INPUT_INVALID;
        }

        if (step.executedAt() > 0 && (System.currentTimeMillis() - step.executedAt()) < combo.minInputDelay()) {
            return InputResult.INPUT_TOO_EARLY;
        }

        return (step.index() + 1 >= inputs.size()) ? InputResult.INPUT_VALID_COMPLETED : InputResult.INPUT_VALID_NEXT;
    }

    private void applyResult(Cache<UUID, ComboStep> cache, UUID playerId, InputResult result, ComboStep step) {
        switch (result) {
            case INPUT_INVALID_STRICT, INPUT_VALID_COMPLETED -> cache.invalidate(playerId);
            case INPUT_VALID_NEXT -> cache.put(playerId, step.next());
        }
    }
}
