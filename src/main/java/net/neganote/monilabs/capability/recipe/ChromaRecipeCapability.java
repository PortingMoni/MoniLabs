package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.Color;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.widgets.layout.Flow;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;

public class ChromaRecipeCapability extends RecipeCapability<ChromaIngredient> {

    public static final ChromaRecipeCapability CAP = new ChromaRecipeCapability();

    protected ChromaRecipeCapability() {
        super(MoniLabs.id("chroma"), 0xFF00FFFF, true, 10, SerializerColor.INSTANCE);
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    @Override
    public ChromaIngredient copyInner(ChromaIngredient content) {
        return content;
    }

    public void buildXEIWidgetContent(IWidget widget, Content content, IO io, boolean perTick,
                                      GTRecipeType recipeType, GTRecipe recipe, int chanceTier, int recipeTier) {
        if (!(widget instanceof Flow flow)) return;

        Color inputColor = ((ChromaIngredient) content.content()).color();
        if (inputColor.isRealColor()) {
            flow.child(Text.lang("monilabs.recipe.required_color",
                    inputColor.getColoredDisplayName())
                    .asWidget());
        } else {
            flow.child(Text.lang("monilabs.recipe.accepted_colors")
                    .asWidget());
            String key = null;
            switch (inputColor) {
                case PRIMARY -> key = "monilabs.recipe.primary_input";
                case SECONDARY -> key = "monilabs.recipe.secondary_input";
                case BASIC -> key = "monilabs.recipe.basic_input";
                case TERTIARY -> key = "monilabs.recipe.tertiary_input";
                case ANY -> key = "monilabs.recipe.any_input_color";
            }
            if (key != null)
                flow.child(Text.lang(key).asWidget());
            else if (inputColor.isTypeNotColor()) {
                Color anticolor = Color.FROM_NOT_COLOR.get(inputColor);
                flow.child(Text.lang("monilabs.recipe.input_color_not", Component.translatable(anticolor.nameKey))
                        .asWidget());
            } else {
                flow.child(Text.lang("monilabs.recipe.mistake_input_colors")
                        .asWidget());
            }
        }
    }

    private static class SerializerColor implements IContentSerializer<ChromaIngredient> {

        public static SerializerColor INSTANCE = new SerializerColor();

        public static final Codec<ChromaIngredient> CODEC = Codec.INT
                .xmap(i -> ChromaIngredient.of(Color.getColorFromKey(i)), color -> color.color().key);

        private SerializerColor() {}

        @Override
        public ChromaIngredient fromJson(JsonElement json) {
            return ChromaIngredient.of(Color.getColorFromKey(json.getAsInt()));
        }

        @Override
        public JsonElement toJson(ChromaIngredient content) {
            return new JsonPrimitive(content.color().key);
        }

        @Override
        public ChromaIngredient of(Object o) {
            if (o instanceof Color color) {
                return ChromaIngredient.of(color);
            } else if (o instanceof ChromaIngredient chroma) {
                return chroma;
            }
            return ChromaIngredient.of(Color.RED);
        }

        @Override
        public ChromaIngredient defaultValue() {
            return ChromaIngredient.of(Color.RED);
        }

        @Override
        public Class<ChromaIngredient> contentClass() {
            return ChromaIngredient.class;
        }

        @Override
        public Codec<ChromaIngredient> codec() {
            return CODEC;
        }
    }
}
