package mod.adrenix.nostalgic.util.client;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.mixin.duck.IWidgetManager;
import mod.adrenix.nostalgic.mixin.widen.AbstractContainerScreenAccessor;
import mod.adrenix.nostalgic.mixin.widen.ScreenAccessor;
import mod.adrenix.nostalgic.util.common.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 */

public abstract class GuiUtil
{
    /**
     * A mod screen supplier (defined in mod loaders)
     */
    @Nullable
    public static Function<Screen, Screen> modScreen = null;

    /* Recipe Button Helpers */

    /**
     * Get a new image button that represents a 'large' recipe button but with a square border.
     * @param screen The current container screen.
     * @param parent The original image button to get data from.
     * @return A new recipe image button as a large square button.
     */
    public static ImageButton getLargeBook(AbstractContainerScreenAccessor screen, ImageButton parent)
    {
        return new ImageButton
        (
            screen.NT$getLeftPos() + 151,
            screen.NT$getTopPos() + 7,
            18,
            18,
            178,
            20,
            ModUtil.Resource.OLD_INVENTORY,
            (button) ->
            {
                parent.onPress();
                ((ImageButton) button).setPosition(screen.NT$getLeftPos() + 151, screen.NT$getTopPos() + 7);
            }
        );
    }

    /**
     * Get a new image button that represents a 'small' recipe button as a square with a question mark in it.
     * @param screen The current container screen.
     * @param parent The original image button to get data from.
     * @return A new recipe image button as a small button with a question mark in it.
     */
    public static ImageButton getSmallBook(AbstractContainerScreenAccessor screen, ImageButton parent)
    {
        return new ImageButton
        (
            screen.NT$getLeftPos() + 160,
            screen.NT$getTopPos() + 7,
            9,
            10,
            178,
            0,
            ModUtil.Resource.OLD_INVENTORY,
            (button) ->
            {
                parent.onPress();
                ((ImageButton) button).setPosition(screen.NT$getLeftPos() + 160, screen.NT$getTopPos() + 7);
            }
        );
    }

    /**
     * Create a new recipe button instance based on the status of recipe button tweaks.
     * @param screen The current container screen.
     * @param book The type of recipe button the user wants.
     */
    public static void createRecipeButton(AbstractContainerScreenAccessor screen, TweakType.RecipeBook book)
    {
        ImageButton recipeButton = null;
        IWidgetManager injector = (IWidgetManager) Minecraft.getInstance().screen;

        for (Widget widget : ((ScreenAccessor) screen).NT$getRenderables())
        {
            if (widget instanceof ImageButton imageButton)
            {
                recipeButton = imageButton;
                break;
            }
        }

        if (injector != null && recipeButton != null)
        {
            switch (book)
            {
                case DISABLED -> recipeButton.setPosition(-9999, -9999);
                case LARGE ->
                {
                    injector.NT$removeWidget(recipeButton);
                    injector.NT$addRenderableWidget(getLargeBook(screen, recipeButton));
                }
                case SMALL ->
                {
                    injector.NT$removeWidget(recipeButton);
                    injector.NT$addRenderableWidget(getSmallBook(screen, recipeButton));
                }
            }
        }
    }

    /* In-game HUD Overlays */

    /**
     * Calculates where text rendering from the right side of the screen should start.
     * @param text The text to render.
     * @return The starting point of where the given text should render.
     */
    private static int getRightX(String text)
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.getWindow().getGuiScaledWidth() - mc.font.width(text) - 2;
    }

    /**
     * Gets a color based on the current food status of the player.
     * @param food The current food level.
     * @return A color code based on the given food level.
     */
    private static String getFoodColor(int food)
    {
        if (food <= 2) return "§4";
        else if (food <= 6) return "§c";
        else if (food <= 10) return "§6";
        else if (food <= 15) return "§e";
        else if (food < 20) return "§2";
        return "§a";
    }

    /**
     * Gets a color based on a given percentage.
     * @param percent The current percentage.
     * @return A color code based on the given percentage.
     */
    private static String getPercentColor(int percent)
    {
        if (percent < 20) return "§c";
        else if (percent < 40) return "§6";
        else if (percent < 60) return "§e";
        else if (percent < 80) return "§2";
        return "§a";
    }

    /**
     * The class will manage and keep track of where given text should render.
     * This greatly simplifies the rendering process of in-game HUD text.
     */
    public static class CornerManager
    {
        private final float height = (float) Minecraft.getInstance().getWindow().getGuiScaledHeight();
        private final AtomicDouble topLeft = new AtomicDouble(2.0D);
        private final AtomicDouble topRight = new AtomicDouble(2.0D);
        private final AtomicDouble bottomLeft = new AtomicDouble(this.height - 10.0D);
        private final AtomicDouble bottomRight = new AtomicDouble(this.height - 10.0D);

        public float getAndAdd(TweakType.Corner corner)
        {
            return switch (corner)
            {
                case TOP_LEFT -> (float) topLeft.getAndAdd(10.0D);
                case TOP_RIGHT -> (float) topRight.getAndAdd(10.0D);
                case BOTTOM_LEFT -> (float) bottomLeft.getAndAdd(-10.0D);
                case BOTTOM_RIGHT -> (float) bottomRight.getAndAdd(-10.0D);
            };
        }
    }

    /**
     * Draws the given text to the in-game HUD.
     * @param poseStack The current pose stack.
     * @param text The text to render.
     * @param corner The corner to render the text to.
     * @param manager The corner manager for this render cycle.
     */
    public static void drawText(PoseStack poseStack, String text, TweakType.Corner corner, CornerManager manager)
    {
        Minecraft mc = Minecraft.getInstance();
        boolean isLeft = corner.equals(TweakType.Corner.TOP_LEFT) || corner.equals(TweakType.Corner.BOTTOM_LEFT);
        mc.font.drawShadow(poseStack, text, isLeft ? 2.0F : getRightX(text), manager.getAndAdd(corner), 0xFFFFFF);
    }

    /**
     * Renders in-game HUD text overlays - game version, food, experience, etc.
     * @param poseStack The current pose stack.
     */
    public static void renderOverlays(PoseStack poseStack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.renderDebug || minecraft.options.hideGui)
            return;

        Player player = minecraft.player;
        if (player == null)
            return;

        CornerManager manager = new CornerManager();

        int foodLevel = player.getFoodData().getFoodLevel();
        int xpPercent = (int) (player.experienceProgress * 100.0F);
        int satPercent = (int) ((player.getFoodData().getSaturationLevel() / 20.0F) * 100.0F);

        if (ModConfig.Candy.oldVersionOverlay())
            drawText(poseStack, ModConfig.Candy.getOverlayText(), ModConfig.Candy.oldOverlayCorner(), manager);

        if (ModConfig.Gameplay.displayAlternativeLevelText())
        {
            TweakType.Corner levelCorner = ModConfig.Gameplay.alternativeLevelCorner();
            String level = ModConfig.Gameplay.getAlternativeLevelText(Integer.toString(player.experienceLevel));
            drawText(poseStack, level, levelCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeProgressText())
        {
            boolean useColor = ModConfig.Gameplay.useDynamicProgressColor();
            TweakType.Corner xpCorner = ModConfig.Gameplay.alternativeProgressCorner();
            String xp = ModConfig.Gameplay.getAlternativeProgressText((useColor ? getPercentColor(xpPercent) : "") + xpPercent);
            drawText(poseStack, xp, xpCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeFoodText())
        {
            boolean useColor = ModConfig.Gameplay.useDynamicFoodColor();
            TweakType.Corner foodCorner = ModConfig.Gameplay.alternativeFoodCorner();
            String food = ModConfig.Gameplay.getAlternativeFoodText((useColor ? getFoodColor(foodLevel) : "") + foodLevel);
            drawText(poseStack, food, foodCorner, manager);
        }

        if (ModConfig.Gameplay.displayAlternativeSatText())
        {
            boolean useColor = ModConfig.Gameplay.useDynamicSatColor();
            TweakType.Corner satCorner = ModConfig.Gameplay.alternativeSaturationCorner();
            String sat = ModConfig.Gameplay.getAlternativeSaturationText((useColor ? getPercentColor(satPercent) : "") + satPercent);
            drawText(poseStack, sat, satCorner, manager);
        }
    }

    /**
     * Renders an inverse half-armor texture.
     * @param poseStack The current pose stack.
     * @param offset The z-offset.
     * @param x The x-position.
     * @param y The y-position.
     * @param uOffset The u-coordinate (horizontal) on the texture sheet.
     * @param vOffset The v-coordinate (vertical) on the texture sheet.
     * @param uWidth The horizontal width of the texture.
     * @param vHeight The vertical height of the texture.
     */
    public static void renderInverseArmor(PoseStack poseStack, float offset, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
    {
        // Flip the vertex’s u texture coordinates so the half armor texture rendering goes from right to left
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, x, y + vHeight, offset).uv((uOffset + uWidth) / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x + uWidth, y + vHeight, offset).uv(uOffset / 256.0F, (vOffset + vHeight) / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x + uWidth, y, offset).uv(uOffset / 256.0F, vOffset / 256.0F).endVertex();
        bufferBuilder.vertex(matrix, x, y, offset).uv((uOffset + uWidth) / 256.0F, vOffset / 256.0F).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
