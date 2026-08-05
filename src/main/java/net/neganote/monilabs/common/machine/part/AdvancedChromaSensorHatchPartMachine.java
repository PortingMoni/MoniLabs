package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.common.machine.multiblock.Color;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
import brachy.modularui.value.sync.BooleanSyncValue;
import brachy.modularui.value.sync.EnumSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import brachy.modularui.widget.ParentWidget;
import brachy.modularui.widgets.ToggleButton;
import brachy.modularui.widgets.layout.Flow;
import brachy.modularui.widgets.menu.DropdownWidget;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.neganote.monilabs.common.machine.multiblock.Color.ACTUAL_COLORS;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedChromaSensorHatchPartMachine extends ChromaSensorHatchPartMachine implements IMuiMachine {

    @Setter
    @Getter
    @SaveField
    @SyncToClient
    public Color detectorColor = Color.RED;

    @Setter
    @Getter
    @SaveField
    @SyncToClient
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

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        EnumSyncValue<Color> colorSyncValue = new EnumSyncValue<>(Color.class,
                this::getDetectorColor, this::setDetectorColor).allowC2S();
        syncManager.syncValue("color", colorSyncValue);
        BooleanSyncValue isInvertedSyncValue = new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S();
        syncManager.syncValue("isInverted", isInvertedSyncValue);

        mainWidget.coverChildren()
                .child(Flow.row()
                        .coverChildren()
                        .margin(6, 8)
                        .childPadding(4)
                        .child(Text.lang("gui.monilabs.chroma.color.display").asWidget())
                        .child(new DropdownWidget<>("colorList", Color.class)
                                .size(90, 18)
                                .value(colorSyncValue)
                                .options(ACTUAL_COLORS)
                                .maxVerticalMenuSize(80)
                                .optionToWidget((color, forSelected) -> Text.str(color.getColoredDisplayName())
                                        .asWidget()
                                        .center()
                                        .padding(2)))
                        .child(new ToggleButton()
                                .size(18)
                                .value(isInvertedSyncValue)
                                .overlay(false, GTGuiTextures.OVERLAY_REDSTONE_OFF)
                                .overlay(true, GTGuiTextures.OVERLAY_REDSTONE_ON)
                                .tooltipAutoUpdate(true)
                                .tooltipDynamic(t -> {
                                    String key = isInvertedSyncValue.getBoolValue() ?
                                            "gui.advanced_chroma_sensor.invert.enabled." :
                                            "gui.advanced_chroma_sensor.invert.disabled.";
                                    for (int i = 0; i < 4; i++) {
                                        t.add(Component.translatable(key + i));
                                    }
                                })));
    }
}
