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

public class PlayerActionManager {

    private final Plugin plugin;
    private final SkillManager skillManager;
    private final Map<String, Cache<UUID, ComboState>> cacheSkill;

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

        skillManager.getSkills().stream()
                .filter(skill -> player.hasPermission(skill.permission()))
                .filter(skill -> test(skill, player, action))
                .filter(skill -> skill.canExecute(player))
                .forEach(skill ->
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            try {
                                skill.execute(player);
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error executing skill " + skill.id() + " for player " + player.getName() + ": " + e.getMessage());
                            }
                        }));
    }

    private Cache<UUID, ComboState> getOrCreateCache(Skill skill) {
        Duration expiration = Duration.ofMillis(skill.combo().maxActionDelay());
        return cacheSkill.computeIfAbsent(skill.id(), id -> CacheBuilder.newBuilder()
                .expireAfterWrite(expiration)
                .build());
    }

    private boolean test(Skill skill, Player player, Action action) {
        UUID playerId = player.getUniqueId();
        Cache<UUID, ComboState> cache = getOrCreateCache(skill);
        Combo combo = skill.combo();

        ComboState state = cache.getIfPresent(playerId);
        if (state == null) {
            state = new ComboState(0, 0L);
        }

        ActionResult result = validateAction(combo, state, action);

        return updateCache(cache, playerId, result, state);
    }

    private ActionResult validateAction(Combo combo, ComboState state, Action action) {
        List<Action> actions = combo.actions();

        if (state.currentIndex() >= actions.size()) {
            return ActionResult.INVALID_ACTION_STRICT;
        }

        if (!actions.get(state.currentIndex()).equals(action)) {
            return combo.strict() ? ActionResult.INVALID_ACTION_STRICT : ActionResult.INVALID_ACTION;
        }

        if (state.lastActionTime() > 0 && (System.currentTimeMillis() - state.lastActionTime()) < combo.minActionDelay()) {
            return ActionResult.TOO_EARLY;
        }

        return (state.currentIndex() + 1 >= actions.size()) ? ActionResult.COMPLETED : ActionResult.NEXT_STEP;
    }

    private boolean updateCache(Cache<UUID, ComboState> cache, UUID playerId, ActionResult result, ComboState state) {
        switch (result) {
            case INVALID_ACTION_STRICT, COMPLETED -> {
                cache.invalidate(playerId);
                return result.isCompleted();
            }
            case INVALID_ACTION, TOO_EARLY -> {
                return result.isCompleted();
            }
            case NEXT_STEP -> {
                cache.put(playerId, new ComboState(state.currentIndex() + 1, System.currentTimeMillis()));
                return result.isCompleted();
            }
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
    }

    private record ComboState(int currentIndex, long lastActionTime) { }

    private enum ActionResult {

        INVALID_ACTION(false),
        INVALID_ACTION_STRICT(false),
        TOO_EARLY(false),
        COMPLETED(true),
        NEXT_STEP(false);

        private final boolean completed;

        ActionResult(boolean completed) {
            this.completed = completed;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}