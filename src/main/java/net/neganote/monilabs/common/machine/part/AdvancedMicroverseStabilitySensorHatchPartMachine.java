package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.ToggleButton;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.textfield.TextFieldWidget;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedMicroverseStabilitySensorHatchPartMachine extends MicroverseStabilitySensorHatchPartMachine
                                                               implements IMuiMachine {

    public static int DEFAULT_MIN_PERCENT = 33;
    public static int DEFAULT_MAX_PERCENT = 66;

    @SaveField
    @SyncToClient
    @Setter
    @Getter
    public int minPercent, maxPercent;

    @SaveField
    @SyncToClient
    @Setter
    @Getter
    public boolean inverted;

    public AdvancedMicroverseStabilitySensorHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
        minPercent = DEFAULT_MIN_PERCENT;
        maxPercent = DEFAULT_MAX_PERCENT;
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction != getFrontFacing().getOpposite()) {
            return 0;
        }

        var controller = (MicroverseProjectorMachine) getController();
        if (controller == null) {
            return 0;
        }

        var actualStability = controller.getMicroverseIntegrity();

        var minStability = minPercent * MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT;
        var maxStability = maxPercent * MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT;

        if (inverted) {
            if (actualStability >= minStability && actualStability <= maxStability) {
                return 0;
            } else {
                return 15;
            }
        } else {
            if (actualStability >= minStability && actualStability <= maxStability) {
                return 15;
            } else {
                return 0;
            }
        }
    }

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        BooleanSyncValue isInvertedSyncValue = new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S();
        syncManager.syncValue("isInverted", isInvertedSyncValue);
        IntSyncValue minPercentSyncValue = new IntSyncValue(this::getMinPercent,
                val -> setMinPercent(Mth.clamp(val, 0, 100))).allowC2S();
        syncManager.syncValue("minPercent", minPercentSyncValue);
        IntSyncValue maxPercentSyncValue = new IntSyncValue(this::getMaxPercent,
                val -> setMaxPercent(Mth.clamp(val, 0, 100))).allowC2S();
        syncManager.syncValue("maxPercent", maxPercentSyncValue);

        mainWidget.coverChildren()
                .child(Flow.col()
                        .coverChildren()
                        .margin(6, 6)
                        .childPadding(3)
                        .crossAxisAlignment(Alignment.CrossAxis.START)
                        .child(Flow.row()
                                .coverChildren()
                                .childPadding(3)
                                .child(new ToggleButton()
                                        .size(18)
                                        .value(isInvertedSyncValue)
                                        .overlay(false, GTGuiTextures.OVERLAY_REDSTONE_OFF)
                                        .overlay(true, GTGuiTextures.OVERLAY_REDSTONE_ON)
                                        .tooltipAutoUpdate(true)
                                        .tooltipDynamic(t -> {
                                            String key = isInvertedSyncValue.getBoolValue() ?
                                                    "gui.monilabs.microverse_stability_sensor.invert.enabled." :
                                                    "gui.monilabs.microverse_stability_sensor.invert.disabled.";
                                            for (int i = 0; i < 4; i++) {
                                                t.add(Component.translatable(key + i));
                                            }
                                        })))
                        .child(thresholdRow("gui.monilabs.microverse_stability.min", minPercentSyncValue,
                                "gui.monilabs.microverse_stability.min_threshold"))
                        .child(thresholdRow("gui.monilabs.microverse_stability.max", maxPercentSyncValue,
                                "gui.monilabs.microverse_stability.max_threshold")));
    }

    private static Flow thresholdRow(String labelKey, IntSyncValue value, String tooltipKey) {
        return Flow.row()
                .coverChildren()
                .childPadding(4)
                .mainAxisAlignment(Alignment.MainAxis.START)
                .child(Text.lang(labelKey).asWidget().width(30))
                .child(new TextFieldWidget()
                        .size(60, 16)
                        .setTextAlignment(Alignment.CENTER)
                        .value(value)
                        .setNumbers(() -> 0, () -> 100)
                        .setDefaultNumber(0)
                        .tooltip(t -> t.add(Component.translatable(tooltipKey))));
    }
}
