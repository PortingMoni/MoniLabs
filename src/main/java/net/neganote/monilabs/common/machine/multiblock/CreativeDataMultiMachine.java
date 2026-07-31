package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeDataAccessSavedData;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public class CreativeDataMultiMachine extends UniqueWorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler creativeDataSubscription;

    public CreativeDataMultiMachine(BlockEntityCreationInfo info) {
        super(info);

        this.creativeDataSubscription = new ConditionalSubscriptionHandler(this, this::tickEnableCreativeData,
                this::isSubscriptionActive);
    }

    @Override
    public void formStructure(@NotNull String substructureName) {
        super.formStructure(substructureName);
        creativeDataSubscription.updateSubscription();
    }

    public void enableCreativeData(boolean enabled) {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) {
            ownerUUID = new UUID(0L, 0L);
        }
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeDataAccessSavedData savedData = CreativeDataAccessSavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            savedData.setEnabled(ownerUUID, enabled);
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        enableCreativeData(false);
    }

    private void tickEnableCreativeData() {
        enableCreativeData(recipeLogic.isWorking());
    }

    private Boolean isSubscriptionActive() {
        return isFormed();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        if (!isWorkingAllowed) {
            enableCreativeData(false);
        }
    }

    @Override
    public void invalidateStructure(@NotNull String name) {
        super.invalidateStructure(name);
        enableCreativeData(false);
        creativeDataSubscription.unsubscribe();
    }
}
