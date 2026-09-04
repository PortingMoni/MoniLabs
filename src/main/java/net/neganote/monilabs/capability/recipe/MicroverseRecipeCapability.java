package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;

import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.Microverse;

import com.mojang.serialization.Codec;

public class MicroverseRecipeCapability extends RecipeCapability<Microverse> {

    public static MicroverseRecipeCapability CAP = new MicroverseRecipeCapability();

    protected MicroverseRecipeCapability() {
        super(MoniLabs.id("microverse"), 0xFF00FFFF, false, 11, SerializerMicroverse.INSTANCE);
    }

    @Override
    public Microverse copyInner(Microverse content) {
        return content;
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    private static class SerializerMicroverse implements IContentSerializer<Microverse> {

        public static SerializerMicroverse INSTANCE = new SerializerMicroverse();

        public static Codec<Microverse> CODEC = Codec.INT.xmap(Microverse::getMicroverseFromKey,
                microverse -> microverse.key);

        @Override
        public Microverse of(Object o) {
            if (!(o instanceof Microverse microverse)) {
                return null;
            }
            return microverse;
        }

        @Override
        public Microverse defaultValue() {
            return Microverse.NONE;
        }

        @Override
        public Class<Microverse> contentClass() {
            return Microverse.class;
        }

        @Override
        public Codec<Microverse> codec() {
            return CODEC;
        }
    }
}
