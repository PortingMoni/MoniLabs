package net.neganote.monilabs.mixin;

import com.gregtechceu.gtceu.common.machine.multiblock.part.RotorHolderPartMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = RotorHolderPartMachine.class, remap = false)
public class RotorHolderMachineMixin {

    /**
     * @author NegaNote
     * @reason killing rotor damage
     */
    @Overwrite
    public void damageRotor(int damageAmount) {}
}
