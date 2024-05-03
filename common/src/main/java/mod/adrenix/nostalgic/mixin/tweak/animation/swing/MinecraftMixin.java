package mod.adrenix.nostalgic.mixin.tweak.animation.swing;

import mod.adrenix.nostalgic.mixin.duck.SwingBlocker;
import mod.adrenix.nostalgic.mixin.util.animation.PlayerArmMixinHelper;
import mod.adrenix.nostalgic.mixin.util.swing.SwingType;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public LocalPlayer player;

    /* Injections */

    /**
     * Resets the swing attack animation tracker.
     */
    @Inject(
        method = "startAttack",
        at = @At("HEAD")
    )
    private void nt_animation_swing$onStartAttack(CallbackInfoReturnable<Boolean> callback)
    {
        PlayerArmMixinHelper.SWING_TYPE.set(SwingType.HIT);

        if (AnimationTweak.OLD_SWING_INTERRUPT.get() && this.player != null)
        {
            this.player.attackAnim = 0.0F;
            this.player.swingTime = 0;
        }
    }

    /**
     * Sets the swing helper swing type tracker to right-click when the player uses an item.
     */
    @Inject(
        method = "startUseItem",
        at = @At("HEAD")
    )
    private void nt_animation_swing$onStartUseItem(CallbackInfo callback)
    {
        PlayerArmMixinHelper.SWING_TYPE.set(SwingType.PLACE);
    }

    /**
     * Sets the animation utility swing type back to left if left-click speed on right-click interact is enabled. The
     * classic swing tweak being enabled will prevent this.
     */
    @Inject(
        method = "startUseItem",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
            )
        ),
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void nt_animation_swing$onStartUseItemOn(CallbackInfo callback)
    {
        if (AnimationTweak.OLD_CLASSIC_PLACE_SWING.get())
            PlayerArmMixinHelper.SWING_TYPE.set(SwingType.PLACE);
    }

    /**
     * Prevents the hand swing animation when dropping an item.
     */
    @Inject(
        method = "handleKeybinds",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private void nt_animation_swing$onDropItem(CallbackInfo callback)
    {
        SwingBlocker swingBlocker = (SwingBlocker) this.player;

        if (AnimationTweak.OLD_SWING_DROPPING.get() && swingBlocker != null)
            swingBlocker.nt$setSwingBlocked(true);
    }
}
