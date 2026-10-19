package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CreativeDataMultiMachine extends UniqueWorkableElectricMultiblockMachine {

    public boolean isProviding = false;

    private final ConditionalSubscriptionHandler creativeSubscription;

    public CreativeDataMultiMachine(BlockEntityCreationInfo info) {
        super(info);
        this.creativeSubscription = new ConditionalSubscriptionHandler(this, this::tickSync, this::isFormed);
    }

    private void tickSync() {
        syncCreativeData(!isDuplicate() && isWorkingEnabled() && recipeLogic.isWorking());
    }

    @Override
    public Class<?> getMachineType() {
        return CreativeDataMultiMachine.class;
    }

    @Override
    public void formStructure(@NotNull String substructureName) {
        super.formStructure(substructureName);
        creativeSubscription.updateSubscription();
        tickSync();
    }

    public void syncCreativeData(boolean active) {
        if (!(getLevel() instanceof ServerLevel)) return;
        if (active == this.isProviding) return;
        this.isProviding = active;
    }

    @Override
    public void onUnload() {
        super.onUnload();
        creativeSubscription.unsubscribe();
        syncCreativeData(false);
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        syncCreativeData(!isDuplicate() && isWorkingEnabled() && recipeLogic.isWorking());
    }

    @Override
    public void invalidateStructure(@NotNull String name) {
        super.invalidateStructure(name);
        creativeSubscription.unsubscribe();
        syncCreativeData(false);
    }
}
