package net.neganote.monilabs.integration.kjs.recipe;

import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;

import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.Microverse;

import com.mojang.datafixers.util.Pair;

@SuppressWarnings("unused")
public class MoniRecipeComponents {

    public static final ChromaComponent CHROMA_COMPONENT = new ChromaComponent();
    public static final ContentJS<Color> CHROMA_IN = new ContentJS<>(CHROMA_COMPONENT,
            MoniRecipeCapabilities.CHROMA, true);

    public static final MicroverseComponent MICROVERSE_COMPONENT = new MicroverseComponent();
    public static final ContentJS<Microverse> MICROVERSE_IN = new ContentJS<>(MICROVERSE_COMPONENT,
            MoniRecipeCapabilities.MICROVERSE, true);

    public static void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(MoniRecipeCapabilities.CHROMA, Pair.of(CHROMA_IN, null));
        event.registerKey(MoniRecipeCapabilities.MICROVERSE, Pair.of(MICROVERSE_IN, null));
    }
}
