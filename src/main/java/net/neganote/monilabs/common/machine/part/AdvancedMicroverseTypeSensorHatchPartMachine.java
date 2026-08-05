package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.api.sync_system.annotations.SyncToClient;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

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
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedMicroverseTypeSensorHatchPartMachine extends MicroverseTypeSensorHatchPartMachine
                                                          implements IMuiMachine {

    @Setter
    @Getter
    @SaveField
    @SyncToClient
    public Microverse detectorMicroverse = Microverse.NONE;

    @Setter
    @Getter
    @SaveField
    @SyncToClient
    public boolean inverted;

    public AdvancedMicroverseTypeSensorHatchPartMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (MicroverseProjectorMachine) getController();

            if (controller == null) {
                return 0;
            }

            var projectorMicroverse = controller.getMicroverse();
            if (inverted) {
                return projectorMicroverse == detectorMicroverse ? 0 : 15;
            } else {
                return projectorMicroverse == detectorMicroverse ? 15 : 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public void buildMainUI(ParentWidget<?> mainWidget, PosGuiData guiData, PanelSyncManager syncManager,
                            UISettings settings) {
        EnumSyncValue<Microverse> microverseSyncValue = new EnumSyncValue<>(Microverse.class,
                this::getDetectorMicroverse, this::setDetectorMicroverse).allowC2S();
        syncManager.syncValue("microverse", microverseSyncValue);
        BooleanSyncValue isInvertedSyncValue = new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S();
        syncManager.syncValue("isInverted", isInvertedSyncValue);

        mainWidget.coverChildren()
                .child(Flow.row()
                        .coverChildren()
                        .margin(6, 8)
                        .childPadding(4)
                        .child(Text.lang("gui.advanced_chroma_sensor.display").asWidget())
                        .child(new DropdownWidget<>("microverseList", Microverse.class)
                                .size(90, 18)
                                .value(microverseSyncValue)
                                .options(Microverse.MICROVERSES)
                                .maxVerticalMenuSize(80)
                                .optionToWidget((microverse, forSelected) -> Text.str(microverse.getDisplayName())
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
                                            "gui.advanced_type_sensor.invert.enabled." :
                                            "gui.advanced_type_sensor.invert.disabled.";
                                    for (int i = 0; i < 4; i++) {
                                        t.add(Component.translatable(key + i));
                                    }
                                })));
    }
}
