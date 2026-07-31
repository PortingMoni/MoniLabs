package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class OmnicSynthesizerMachine extends WorkableElectricMultiblockMachine {

    @SaveField
    public List<Item> diversityList = new ArrayList<>();

    @SaveField
    @DescSynced
    public int diversityPoints = 0;

    @SaveField
    public boolean recipeModifierCalculated = false;

    @SaveField
    public double recipeModifierAmount = 0.0;

    public OmnicSynthesizerMachine(BlockEntityCreationInfo info) {
        super(info);
    }

    @Override
    public GTRecipe fullModifyRecipe(GTRecipe recipe) {
        return super.fullModifyRecipe(recipe);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        recipeModifierCalculated = false;
        recipeModifierAmount = 0.0;
    }
}
