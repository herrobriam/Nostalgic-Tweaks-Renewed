package mod.adrenix.nostalgic.init;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.gameplay.MobLootHelper;
import mod.adrenix.nostalgic.listener.common.InteractionListener;
import mod.adrenix.nostalgic.listener.common.PlayerListener;
import mod.adrenix.nostalgic.network.PacketRegistry;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.server.ServerTimer;

public abstract class ModInitializer
{
    /**
     * Registers common mod events.
     */
    public static void register()
    {
        PacketRegistry.register();
        InteractionListener.register();
        PlayerListener.register();

        LifecycleEvent.SERVER_BEFORE_START.register(NostalgicTweaks::setServer);
        TickEvent.SERVER_PRE.register(server -> ServerTimer.getInstance().onTick());
        TickEvent.SERVER_POST.register(server -> TweakPool.stream().forEach(Tweak::invalidate));

        EnvExecutor.runInEnv(Env.CLIENT, () -> ClientInitializer::register);

        MobLootHelper.init();
    }
}
