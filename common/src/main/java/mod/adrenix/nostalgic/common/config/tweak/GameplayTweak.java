package mod.adrenix.nostalgic.common.config.tweak;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.reflect.GroupType;
import mod.adrenix.nostalgic.server.config.reflect.TweakServerCache;

public enum GameplayTweak implements ITweak
{
    // Bugs
    LADDER_GAP,
    SQUID_MILK,

    // Mob System
    ANIMAL_CAP,
    ANIMAL_PANIC,
    ANIMAL_SPAWNING,
    SHEEP_PUNCHING,
    ONE_WOOL_PUNCH,

    // Combat System
    INSTANT_BOW,
    INVINCIBLE_BOW,
    ARROW_SPEED,
    DISABLE_COOLDOWN,
    DISABLE_MISS_TIMER,
    DISABLE_SWEEP,

    // Experience System
    DISABLE_EXP_BAR,
    SHOW_XP_LEVEL,
    XP_LEVEL_CORNER,
    XP_LEVEL_TEXT,
    SHOW_XP_PROGRESS,
    USE_DYNAMIC_PROGRESS_COLOR,
    XP_PROGRESS_CORNER,
    XP_PROGRESS_TEXT,
    ORB_SPAWN,
    ORB_RENDERING,
    ANVIL,
    ENCHANT_TABLE,

    // Hunger System
    DISABLE_HUNGER_BAR,
    SHOW_HUNGER_FOOD,
    USE_DYNAMIC_FOOD_COLOR,
    HUNGER_FOOD_CORNER,
    HUNGER_FOOD_TEXT,
    SHOW_HUNGER_SATURATION,
    USE_DYNAMIC_SATURATION_COLOR,
    HUNGER_SATURATION_CORNER,
    HUNGER_SATURATION_TEXT,
    HUNGER,
    INSTANT_EAT,
    FOOD_STACKING,

    // Game Mechanics
    SPRINT,
    SWIM,
    FIRE_SPREAD,
    INSTANT_AIR,
    LEFT_CLICK_DOOR,
    LEFT_CLICK_LEVER,
    LEFT_CLICK_BUTTON,
    TILLED_GRASS_SEEDS,
    INSTANT_BONE_MEAL,
    INFINITE_BURN;

    /* Implementation */

    private String key;
    private NostalgicTweaks.Side side = null;
    private TweakClientCache<?> clientCache;
    private TweakServerCache<?> serverCache;
    private boolean loaded = false;

    @Override public String getKey() { return this.key; }
    @Override public GroupType getGroup() { return GroupType.GAMEPLAY; }

    @Override public void setSide(NostalgicTweaks.Side side) { this.side = side; }
    @Override public NostalgicTweaks.Side getSide() { return this.side; }

    @Override public void setClientCache(TweakClientCache<?> cache) { this.clientCache = cache; }
    @Override public void setServerCache(TweakServerCache<?> cache) { this.serverCache = cache; }
    @Override public TweakServerCache<?> getServerCache() { return this.serverCache; }
    @Override public TweakClientCache<?> getClientCache() { return this.clientCache; }

    @Override public boolean isLoaded() { return this.loaded; }
    @Override public void setLoaded(boolean state) { this.loaded = state; }
    @Override public void setKey(String key) { this.key = key; }
}
