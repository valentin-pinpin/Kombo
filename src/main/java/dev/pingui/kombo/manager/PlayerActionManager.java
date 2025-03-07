package dev.pingui.kombo.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.pingui.kombo.action.ActionState;
import dev.pingui.kombo.action.ActionType;
import dev.pingui.kombo.action.Action;
import dev.pingui.kombo.skill.Skill;
import dev.pingui.kombo.combo.Combo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class PlayerActionManager {

    private final Plugin plugin;
    private final SkillManager skillManager;
    private final Map<String, Cache<UUID, ComboStep>> cacheSkill;

    public PlayerActionManager(Plugin plugin, SkillManager skillManager) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        this.skillManager = Objects.requireNonNull(skillManager, "SkillManager cannot be null");
        this.cacheSkill = new HashMap<>();
    }

    public void playerAction(Player player, ActionType type, ActionState state) {
        playerAction(player, new Action(type, state));
    }

    public void playerAction(Player player, Action action) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        skillManager.skills().stream()
                .filter(skill -> player.hasPermission(skill.permission()))
                .filter(skill -> test(skill, player, action))
                .filter(skill -> skill.canExecute(player))
                .forEach(skill -> executeSkill(player, skill));
    }

    private void executeSkill(Player player, Skill skill) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                skill.execute(player);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error executing skill " + skill.id() + " for player " + player.getName(), e);
            }
        });
    }

    private Cache<UUID, ComboStep> getOrCreateCache(Skill skill) {
        Duration expiration = Duration.ofMillis(skill.combo().maxActionDelay());
        return cacheSkill.computeIfAbsent(skill.id(), id -> CacheBuilder.newBuilder()
                .expireAfterWrite(expiration)
                .build());
    }

    private boolean test(Skill skill, Player player, Action action) {
        UUID playerId = player.getUniqueId();
        Cache<UUID, ComboStep> cache = getOrCreateCache(skill);
        Combo combo = skill.combo();

        ComboStep step = cache.getIfPresent(playerId);
        if (step == null) {
            step = new ComboStep(0, 0L);
        }

        ActionResult result = validateAction(combo, step, action);
        updateCache(cache, playerId, result, step);

        return result.isCompleted();
    }

    private ActionResult validateAction(Combo combo, ComboStep step, Action action) {
        List<Action> actions = combo.actions();

        if (step.index() >= actions.size()) {
            return ActionResult.INVALID_ACTION_STRICT;
        }

        if (!actions.get(step.index()).equals(action)) {
            return combo.strict() ? ActionResult.INVALID_ACTION_STRICT : ActionResult.INVALID_ACTION;
        }

        if (step.executedAt() > 0 && (System.currentTimeMillis() - step.executedAt()) < combo.minActionDelay()) {
            return ActionResult.TOO_EARLY;
        }

        return (step.index() + 1 >= actions.size()) ? ActionResult.COMPLETED : ActionResult.NEXT_STEP;
    }

    private void updateCache(Cache<UUID, ComboStep> cache, UUID playerId, ActionResult result, ComboStep step) {
        switch (result) {
            case INVALID_ACTION_STRICT, COMPLETED -> cache.invalidate(playerId);
            case NEXT_STEP -> cache.put(playerId, step.next());
        }
    }

    private record ComboStep(int index, long executedAt) {

        public ComboStep next() {
            return new ComboStep(index + 1, System.currentTimeMillis());
        }
    }

    private enum ActionResult {

        INVALID_ACTION,
        INVALID_ACTION_STRICT,
        TOO_EARLY,
        NEXT_STEP,
        COMPLETED;

        public boolean isCompleted() {
            return this == COMPLETED;
        }
    }
}