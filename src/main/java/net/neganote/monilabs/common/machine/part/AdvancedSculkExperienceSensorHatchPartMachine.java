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

        mainWidget.child(Flow.col()
                .child(new ToggleButton()
                        .value(isInvertedSyncValue)
                        .overlay(false, GTGuiTextures.OVERLAY_REDSTONE_OFF)
                        .overlay(true, GTGuiTextures.OVERLAY_REDSTONE_ON)
                        .tooltipDynamic(t -> {
                            if (isInvertedSyncValue.getBoolValue()) {
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.enabled.0"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.enabled.1"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.enabled.2"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.enabled.3"));
                            } else {
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.disabled.0"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.disabled.1"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.disabled.2"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.invert.disabled.3"));
                            }
                        }))
                .child(new ToggleButton()
                        .value(usesPercentSyncValue)
                        .overlay(MoniGuiTextures.XP_SENSOR_BUTTON.main())
                        .tooltipDynamic(t -> {
                            if (isInvertedSyncValue.getBoolValue()) {
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.enabled.0"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.enabled.1"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.enabled.2"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.enabled.3"));
                            } else {
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.disabled.0"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.disabled.1"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.disabled.2"));
                                t.add(Component.translatable("gui.monilabs.xp_sensor.mode_toggle.disabled.3"));
                            }
                        }))
                .child(Text.dynamic(() -> usesPercentSyncValue.getBoolValue() ?
                        Component.translatable("gui.monilabs.xp_sensor.mode_percentage") :
                        Component.translatable("gui.monilabs.xp_sensor.mode_raw_amount")).asWidget())
                .child(Text.lang("gui.monilabs.xp_sensor.min").asWidget())
                .child(new TextFieldWidget()
                        .value(new IntValue.Dynamic(
                                () -> usesPercentSyncValue.getBoolValue() ? minPercentSyncValue.getIntValue() :
                                        minValueSyncValue.getIntValue(),
                                val -> {
                                    if (usesPercentSyncValue.getBoolValue()) {
                                        minPercentSyncValue.setIntValue(val);
                                    } else {
                                        minValueSyncValue.setIntValue(val);
                                    }
                                }))
                        .setNumbers(() -> 0,
                                () -> usesPercentSyncValue.getBoolValue() ? 100 : SculkVatMachine.XP_BUFFER_MAX)
                        .tooltip(t -> t.add(Component.translatable("gui.monilabs.xp_sensor.min_threshold"))))
                .child(Text.lang("gui.monilabs.xp_sensor.max").asWidget())
                .child(new TextFieldWidget()
                        .value(new IntValue.Dynamic(
                                () -> usesPercentSyncValue.getBoolValue() ? maxPercentSyncValue.getIntValue() :
                                        maxValueSyncValue.getIntValue(),
                                val -> {
                                    if (usesPercentSyncValue.getBoolValue()) {
                                        maxPercentSyncValue.setIntValue(val);
                                    } else {
                                        maxValueSyncValue.setIntValue(val);
                                    }
                                }))
                        .setNumbers(() -> 0,
                                () -> usesPercentSyncValue.getBoolValue() ? 100 : SculkVatMachine.XP_BUFFER_MAX)
                        .tooltip(t -> t.add(Component.translatable("gui.monilabs.xp_sensor.max_threshold")))));
    }
}
