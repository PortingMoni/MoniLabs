package net.neganote.monilabs.client.gui;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.recipe.gui.ProgressBarTextureSet;

import net.neganote.monilabs.MoniLabs;

import brachy.modularui.drawable.ColorType;
import brachy.modularui.drawable.UITexture;

public class MoniGuiTextures {

    private static UITexture progressBar(String path) {
        UITexture.Builder builder = new UITexture.Builder()
                .location(MoniLabs.MOD_ID, path)
                .imageSize(40, 20)
                .colorType(ColorType.DEFAULT);
        return builder.build();
    }

    public static final ProgressBarTextureSet PROGRESS_BAR_RECONSTRUCTION = new ProgressBarTextureSet(
            progressBar("textures/gui/progress_bar/progress_bar_reconstruction.png"));

    public static final ProgressBarTextureSet PROGRESS_BAR_ROCKET = new ProgressBarTextureSet(
            progressBar("textures/gui/progress_bar/progress_bar_rocket.png"));

    public static final ProgressBarTextureSet PROGRESS_BAR_SIMULATION = new ProgressBarTextureSet(
            progressBar("textures/gui/progress_bar/progress_bar_simulation.png"));

    public static final ProgressBarTextureSet PROGRESS_BAR_XP = new ProgressBarTextureSet(
            progressBar("textures/gui/progress_bar/progress_bar_xp.png"));

    public static final ProgressBarTextureSet XP_SENSOR_BUTTON = new ProgressBarTextureSet(
            progressBar("textures/gui/widget/button_xp_sensor.png"));

    public static final UITexture ALWAYS_FULL_ARROW_TEXTURE = UITexture.builder()
            .location(GTCEu.id("textures/gui/progress_bar/progress_bar_arrow.png"))
            .imageSize(20, 40)
            .subAreaUV(0, 0.5f, 1, 1) // We want only the second texture
            .build();
    public static final ProgressBarTextureSet ALWAYS_FULL_ARROW = new ProgressBarTextureSet(ALWAYS_FULL_ARROW_TEXTURE);
}
