package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.client.gui.MoniGuiTextures;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.utils.Alignment;
import brachy.modularui.value.IntValue;
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

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedSculkExperienceSensorHatchPartMachine extends SculkExperienceSensorHatchPartMachine
                                                           implements IMuiMachine {

    @SaveField
    @Setter
    @Getter
    public int minPercent = 33, maxPercent = 66;

    @SaveField
    @Setter
    @Getter
    public int minValue = 1000, maxValue = 10000;

    @SaveField
    @Setter
    @Getter
    public boolean inverted;

    @SaveField
    @Setter
    @Getter
    public boolean usesPercent = true;

    public AdvancedSculkExperienceSensorHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction != getFrontFacing().getOpposite()) return 0;

        var controller = (SculkVatMachine) getController();
        if (controller == null) return 0;

        int currentXP = controller.getXpBuffer();
        int min, max;

        if (usesPercent) {
            min = (int) (SculkVatMachine.XP_BUFFER_MAX * (minPercent / 100.0));
            max = (int) (SculkVatMachine.XP_BUFFER_MAX * (maxPercent / 100.0));
        } else {
            min = minValue;
            max = maxValue;
        }

        boolean inRange = (currentXP >= min && currentXP <= max);
        return (inverted != inRange) ? 15 : 0;
    }

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        BooleanSyncValue isInvertedSyncValue = new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S();
        syncManager.syncValue("isInverted", isInvertedSyncValue);
        BooleanSyncValue usesPercentSyncValue = new BooleanSyncValue(this::isUsesPercent, this::setUsesPercent)
                .allowC2S();
        syncManager.syncValue("usesPercent", usesPercentSyncValue);
        IntSyncValue minPercentSyncValue = new IntSyncValue(this::getMinPercent, this::setMinPercent).allowC2S();
        syncManager.syncValue("minPercent", minPercentSyncValue);
        IntSyncValue minValueSyncValue = new IntSyncValue(this::getMinValue, this::setMinValue).allowC2S();
        syncManager.syncValue("minValue", minValueSyncValue);
        IntSyncValue maxPercentSyncValue = new IntSyncValue(this::getMaxPercent, this::setMaxPercent).allowC2S();
        syncManager.syncValue("maxPercent", maxPercentSyncValue);
        IntSyncValue maxValueSyncValue = new IntSyncValue(this::getMaxValue, this::setMaxValue).allowC2S();
        syncManager.syncValue("maxValue", maxValueSyncValue);

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
                                                    "gui.monilabs.xp_sensor.invert.enabled." :
                                                    "gui.monilabs.xp_sensor.invert.disabled.";
                                            for (int i = 0; i < 4; i++) {
                                                t.add(Component.translatable(key + i));
                                            }
                                        }))
                                .child(new ToggleButton()
                                        .size(18)
                                        .value(usesPercentSyncValue)
                                        .overlay(false, MoniGuiTextures.XP_SENSOR_BUTTON_RAW)
                                        .overlay(true, MoniGuiTextures.XP_SENSOR_BUTTON_PERCENT)
                                        .tooltipAutoUpdate(true)
                                        .tooltipDynamic(t -> {
                                            String key = usesPercentSyncValue.getBoolValue() ?
                                                    "gui.monilabs.xp_sensor.mode_toggle.enabled." :
                                                    "gui.monilabs.xp_sensor.mode_toggle.disabled.";
                                            for (int i = 0; i < 4; i++) {
                                                t.add(Component.translatable(key + i));
                                            }
                                        }))
                                .child(Text.dynamic(() -> usesPercentSyncValue.getBoolValue() ?
                                        Component.translatable("gui.monilabs.xp_sensor.mode_percentage") :
                                        Component.translatable("gui.monilabs.xp_sensor.mode_raw_amount"))
                                        .asWidget()
                                        .marginLeft(2)))
                        .child(thresholdRow("gui.monilabs.xp_sensor.min", usesPercentSyncValue,
                                minPercentSyncValue, minValueSyncValue, "gui.monilabs.xp_sensor.min_threshold"))
                        .child(thresholdRow("gui.monilabs.xp_sensor.max", usesPercentSyncValue,
                                maxPercentSyncValue, maxValueSyncValue, "gui.monilabs.xp_sensor.max_threshold")));
    }

    private static Flow thresholdRow(String labelKey, BooleanSyncValue usesPercent, IntSyncValue percentValue,
                                     IntSyncValue rawValue, String tooltipKey) {
        return Flow.row()
                .coverChildren()
                .childPadding(4)
                .mainAxisAlignment(Alignment.MainAxis.START)
                .child(Text.lang(labelKey).asWidget().width(30))
                .child(new TextFieldWidget()
                        .size(60, 16)
                        .setTextAlignment(Alignment.CENTER)
                        .value(new IntValue.Dynamic(
                                () -> usesPercent.getBoolValue() ? percentValue.getIntValue() : rawValue.getIntValue(),
                                val -> {
                                    if (usesPercent.getBoolValue()) {
                                        percentValue.setIntValue(val);
                                    } else {
                                        rawValue.setIntValue(val);
                                    }
                                }))
                        .setNumbers(() -> 0,
                                () -> usesPercent.getBoolValue() ? 100 : SculkVatMachine.XP_BUFFER_MAX)
                        .setDefaultNumber(0)
                        .tooltip(t -> t.add(Component.translatable(tooltipKey))));
    }
}
