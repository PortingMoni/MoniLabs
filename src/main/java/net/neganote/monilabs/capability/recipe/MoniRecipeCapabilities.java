package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;

import net.minecraft.resources.ResourceLocation;

public class MoniRecipeCapabilities {

    public static final ChromaRecipeCapability CHROMA = ChromaRecipeCapability.CAP;
    public static final MicroverseRecipeCapability MICROVERSE = MicroverseRecipeCapability.CAP;

    public static void init(GTCEuAPI.RegisterEvent<ResourceLocation, RecipeCapability<?>> event) {
        event.register(CHROMA.id, CHROMA);
        event.register(MICROVERSE.id, MICROVERSE);
    }
}
