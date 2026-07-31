package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.config.MoniConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class SculkExperienceDrainingHatchPartMachine extends FluidHatchPartMachine {

    public SculkExperienceDrainingHatchPartMachine(BlockEntityCreationInfo info) {
        super(info, GTValues.ZPM, IO.IN, FluidType.BUCKET_VOLUME, 1);
    }

    @Override
    protected @NotNull NotifiableFluidTank createTank(int initialCapacity, int slots) {
        return super.createTank(initialCapacity, slots).setFilter(fluidStack -> fluidStack.getFluid().isSame(
                Objects.requireNonNull(ForgeRegistries.FLUIDS
                        .getValue(ResourceLocation.bySeparator(MoniConfig.INSTANCE.values.sculkVatExperienceFluidID,
                                ':')))));
    }

    @Override
    public boolean swapIO() {
        return false;
    }

    @Override
    public boolean canShared(@NotNull MultiblockControllerMachine controller, @NotNull String substructureName) {
        return false;
    }
}
