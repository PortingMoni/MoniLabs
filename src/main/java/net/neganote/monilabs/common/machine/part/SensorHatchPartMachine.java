package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SensorHatchPartMachine extends TieredPartMachine {

    private final ConditionalSubscriptionHandler signalUpdateHandler;

    public SensorHatchPartMachine(BlockEntityCreationInfo info, int tier) {
        super(info, tier);
        this.signalUpdateHandler = new ConditionalSubscriptionHandler(this, this::updateSignal, () -> true);
    }

    @Override
    public boolean canConnectRedstone(@NotNull Direction side) {
        return side == getFrontFacing();
    }

    // Really gross but unfortunately not much we can do to force the redstone updates since it's pull-based it seems
    public void updateSignal() {
        if (!isRemote()) {
            notifyBlockUpdate();
        }
    }

    @Override
    public void removedFromController(@NotNull MultiblockControllerMachine controller) {
        super.removedFromController(controller);
        signalUpdateHandler.updateSubscription();
    }

    @Override
    public void addedToController(@NotNull MultiblockControllerMachine controller, @NotNull String substructureName) {
        super.addedToController(controller, substructureName);
        signalUpdateHandler.updateSubscription();
    }

    public @Nullable MultiblockControllerMachine getController() {
        var controllers = getControllers().stream().toList();
        if (controllers.isEmpty() || !controllers.get(0).isFormed()) {
            return null;
        } else {
            return controllers.get(0);
        }
    }

    @Override
    public boolean canShared(@NotNull MultiblockControllerMachine controller, @NotNull String substructureName) {
        return false;
    }
}
