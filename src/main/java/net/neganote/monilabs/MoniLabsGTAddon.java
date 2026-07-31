package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.recipe.MoniRecipes;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class MoniLabsGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return MoniLabs.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return MoniLabs.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        MoniRecipes.init(provider);
    }

    @Override
    public boolean requiresHighTier() {
        return true;
    }
}
