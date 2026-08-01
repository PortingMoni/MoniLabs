package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.feature.IMuiMachine;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.factory.PosGuiData;
import brachy.modularui.screen.UISettings;
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
    @Setter
    @Getter
    public int minPercent, maxPercent;

    @SaveField
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
        Flow column = Flow.column()
                .child(new ToggleButton()
                        .value(new BooleanSyncValue(this::isInverted, this::setInverted).allowC2S())
                        .overlay(false, GTGuiTextures.OVERLAY_REDSTONE_OFF)
                        .overlay(true, GTGuiTextures.OVERLAY_REDSTONE_ON)
                        .tooltip(t -> t.add("gui.monilabs.microverse_stability_sensor.invert")))
                .child(Flow.row()
                        .child(Text.lang("gui.monilabs.microverse_stability.min").asWidget())
                        .child(new TextFieldWidget()
                                .setNumbers(0, 100)
                                .value(new IntSyncValue(() -> minPercent,
                                        val -> minPercent = Mth.clamp(minPercent, 0, 100)).allowC2S())
                                .tooltip(t -> t.add("gui.monilabs.microverse_stability.min_threshold"))))
                .child(Flow.row()
                        .child(Text.lang("gui.monilabs.microverse_stability.max").asWidget())
                        .child(new TextFieldWidget()
                                .setNumbers(0, 100)
                                .value(new IntSyncValue(() -> maxPercent,
                                        val -> maxPercent = Mth.clamp(maxPercent, 0, 100)).allowC2S())
                                .tooltip(t -> t.add("gui.monilabs.microverse_stability.max_threshold"))));
        mainWidget.child(column);
    }
}
