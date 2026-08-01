package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.common.machine.multiblock.Color;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.BoolValue;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.EnumSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widget.Widget;
import brachy.modularui.widgets.ToggleButton;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.menu.ContextMenuButton;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.neganote.monilabs.common.machine.multiblock.Color.ACTUAL_COLORS;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedChromaSensorHatchPartMachine extends ChromaSensorHatchPartMachine implements IMuiMachine {

    @Setter
    @Getter
    @SaveField
    public Color detectorColor = Color.RED;

    @Setter
    @Getter
    @SaveField
    public boolean inverted = false;

    public AdvancedChromaSensorHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    public int getOutputSignal(Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var prismacColor = getPrismacColor();
            if (prismacColor == null) {
                return 0;
            }
            if (inverted) {
                return prismacColor == detectorColor ? 0 : 15;
            } else {
                return prismacColor == detectorColor ? 15 : 0;
            }
        } else {
            return 0;
        }
    }

    private static List<String> displayNames = Arrays.stream(ACTUAL_COLORS)
            .map(Color::getColoredDisplayName)
            .toList();

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        EnumSyncValue<Color> colorSyncValue = new EnumSyncValue<>(Color.class,
                this::getDetectorColor, this::setDetectorColor).allowC2S();
        syncManager.syncValue("color", colorSyncValue);
        BooleanSyncValue isInvertedSyncValue = new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S();
        syncManager.syncValue("isInverted", isInvertedSyncValue);

        mainWidget.child(Flow.col()
                .child(Text.lang("gui.monilabs.chroma.color.display").asWidget()))
                .child(new ContextMenuButton<>("colorList")
                        .menuList(l -> l.height(60)
                                .children(ACTUAL_COLORS.length, i -> colorButton(colorSyncValue, i))))
                .child(new ToggleButton()
                        .value(isInvertedSyncValue)
                        .overlay(false, GTGuiTextures.OVERLAY_REDSTONE_OFF)
                        .overlay(true, GTGuiTextures.OVERLAY_REDSTONE_ON)
                        .tooltipDynamic(t -> {
                            if (isInvertedSyncValue.getBoolValue()) {
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.enabled.0"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.enabled.1"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.enabled.2"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.enabled.3"));
                            } else {
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.disabled.0"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.disabled.1"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.disabled.2"));
                                t.add(Component.translatable("gui.advanced_chroma_sensor.invert.disabled.3"));
                            }
                        }));
    }

    private Widget<?> colorButton(EnumSyncValue<Color> syncValue, int i) {
        return new ToggleButton().size(18)
                .value(boolValueOf(syncValue, ACTUAL_COLORS[i]))
                .overlay(Text.str(displayNames.get(i)));
    }

    BoolValue.Dynamic boolValueOf(EnumSyncValue<Color> syncValue, Color value) {
        return new BoolValue.Dynamic(() -> syncValue.getValue() == value, $ -> syncValue.setValue(value));
    }
}
