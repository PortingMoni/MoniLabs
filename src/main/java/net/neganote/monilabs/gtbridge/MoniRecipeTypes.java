package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.common.mui.GTGuiTextures;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.client.gui.MoniGuiTextures;
import net.neganote.monilabs.common.data.MoniSounds;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import brachy.modularui.api.drawable.Text;

@SuppressWarnings("unused")
public class MoniRecipeTypes {

    public static GTRecipeType createPrismaCRecipeType(String name) {
        return GTRecipeTypes
                .register(name, GTRecipeTypes.MULTIBLOCK)
                .setMaxSize(IO.BOTH, MoniRecipeCapabilities.CHROMA, 1)

                .UI(builder -> builder
                        .setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                        .addRecipeUIModifier((recipe, widget) -> {
                            widget.textComponents.child(Text.dynamic(() -> {
                                var data = recipe.data;

                                if (!data.contains("output_states")) {
                                    return Component.translatable("monilabs.recipe.fully_random_color");
                                }
                                boolean isRelative = (data.contains("color_change_relative") &&
                                        data.getBoolean("color_change_relative"));
                                int outputStatesCount = data.getInt("output_states");

                                if (outputStatesCount == Color.COLOR_COUNT) {
                                    return Component.translatable("monilabs.recipe.fully_random_color");
                                }

                                if (outputStatesCount == 1) {
                                    int modulus = data.getInt("output_states_0");
                                    if (isRelative) {
                                        return Component.translatable("monilabs.recipe.result_color_relative", modulus);
                                    } else {
                                        return Component.translatable("monilabs.recipe.result_color",
                                                Component.translatable(Color.getColorFromKey(modulus).nameKey));
                                    }
                                }
                                StringBuilder strBuilder;
                                if (isRelative) {
                                    strBuilder = new StringBuilder(
                                            Component.translatable("monilabs.recipe.color_list_random_start_relative")
                                                    .getString());
                                } else {
                                    strBuilder = new StringBuilder(
                                            Component.translatable("monilabs.recipe.color_list_random_start")
                                                    .getString());
                                }
                                for (int i = 0; i < outputStatesCount; i++) {
                                    strBuilder.append(Component.translatable(
                                            Color.getColorFromKey(data.getInt("output_states_" + i)).nameKey));
                                    if (i % 3 == 2) {
                                        strBuilder.append("\n");
                                    } else {
                                        strBuilder.append(
                                                Component.translatable("monilabs.recipe.color_list_separator")
                                                        .getString());
                                    }

                                }
                                return Component.literal(strBuilder.toString());
                            }).asWidget());
                        }))

                .setMaxTooltips(8)
                .setMaxIOSize(3, 3, 1, 1)
                .setEUIO(IO.IN);
    }

    public static GTRecipeType CHROMATIC_PROCESSING = createPrismaCRecipeType("chromatic_processing");
    public static GTRecipeType CHROMATIC_TRANSCENDENCE = createPrismaCRecipeType("chromatic_transcendence");

    public static GTRecipeType MICROVERSE_RECIPES = GTRecipeTypes
            .register("microverse", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxSize(IO.IN, MoniRecipeCapabilities.MICROVERSE, 1)
            .setMaxIOSize(9, 9, 3, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 8, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(MoniGuiTextures.PROGRESS_BAR_ROCKET)
                    .addRecipeUIModifier((recipe, widget) -> {
                        widget.textComponents.child(
                                Text.lang("emi_info.monilabs.projector_info", recipe.data.getByte("projector_tier"))
                                        .asWidget())
                                .child(Text.dynamic(() -> {
                                    if (!recipe.data.contains("updated_microverse")) return Component.empty();
                                    return Component.translatable("emi_info.monilabs.new_microverse",
                                            Component.translatable(Microverse.values()[recipe.data
                                                    .getInt("updated_microverse")].langKey));
                                })
                                        .asWidget()
                                        .setEnabledIf(w -> recipe.data.contains("updated_microverse")))
                                .child(Text.dynamic(() -> {
                                    if (!recipe.data.contains("damage_rate") || recipe.data.getInt("damage_rate") == 0)
                                        return Component.empty();
                                    var damageRate = recipe.data.getInt("damage_rate");
                                    if (damageRate > 0) {
                                        return Component.translatable("emi_info.monilabs.integrity_drained",
                                                (float) (damageRate *
                                                        recipe.data.getInt("duration")) /
                                                        MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT);
                                    } else {
                                        return Component.translatable("emi_info.monilabs.integrity_healed",
                                                (float) (-damageRate *
                                                        recipe.data.getInt("duration")) /
                                                        MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT);
                                    }
                                })
                                        .asWidget()
                                        .setEnabledIf(w -> recipe.data.contains("damage_rate") &&
                                                recipe.data.getInt("damage_rate") != 0))
                                .child(Text.lang("emi_info.monilabs.cannot_parallel")
                                        .asWidget()
                                        .setEnabledIf(w -> recipe.data.contains("blacklistParallel") &&
                                                recipe.data.getBoolean("blacklistParallel")));
                    }))
            .setSound(MoniSounds.MICROVERSE);

    public static GTRecipeType ATOMIC_RECONSTRUCTOR = GTRecipeTypes
            .register("atomic_reconstruction", "reconstruction")
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 1, 0, 0)
            .setSound(GTSoundEntries.ELECTROLYZER)
            .UI(builder -> builder.setProgressBar(MoniGuiTextures.PROGRESS_BAR_RECONSTRUCTION)
                    .setItemSlotOverlay(IO.IN, 1, GTGuiTextures.ARROW_INPUT_OVERLAY));

    public static GTRecipeType ANTIMATTER_COLLIDER_RECIPES = GTRecipeTypes
            .register("anti_collider", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.OUT)
            .setMaxIOSize(0, 0, 2, 0)
            .UI(builder -> builder.setProgressBar(MoniGuiTextures.ALWAYS_FULL_ARROW));

    public static GTRecipeType CREATIVE_ENERGY_MULTI_RECIPES = GTRecipeTypes
            .register("creative_energy_multi", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 2, 0)
            .UI(builder -> builder.setProgressBar(MoniGuiTextures.ALWAYS_FULL_ARROW)
                    .addRecipeUIModifier((recipe, widget) -> {
                        widget.textComponents.child(Text.lang("emi_info.creative_energy_multi.0").asWidget())
                                .child(Text.lang("emi_info.creative_energy_multi.1").asWidget());
                    }))
            .setSound(GTSoundEntries.COMBUSTION);

    public static GTRecipeType CREATIVE_DATA_MULTI_RECIPES = GTRecipeTypes
            .register("creative_data_multi", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(0, 0, 1, 0)
            .UI(builder -> builder.setProgressBar(MoniGuiTextures.ALWAYS_FULL_ARROW)
                    .addRecipeUIModifier((recipe, widget) -> {
                        widget.textComponents.child(Text.lang("emi_info.creative_data_multi.0").asWidget())
                                .child(Text.lang("emi_info.creative_data_multi.1").asWidget())
                                .child(Text.lang("emi_info.creative_data_multi.2").asWidget());
                    }))
            .setSound(GTSoundEntries.COMPUTATION);

    public static GTRecipeType OMNIC_SYNTHESIS = GTRecipeTypes.register("omnic_synthesis", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 1, 0, 0)
            .setSound(GTSoundEntries.CHEMICAL)
            .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW)
                    .addRecipeUIModifier((recipe, widget) -> widget.textComponents
                            .child(Text.lang("gtceu.multiblock.omnic_synthesizer.emi_info.0").asWidget())
                            .child(Text.lang("gtceu.multiblock.omnic_synthesizer.emi_info.1").asWidget())
                            .child(Text.lang("gtceu.multiblock.omnic_synthesizer.emi_info.2").asWidget())
                            .child(Text.lang("gtceu.multiblock.omnic_synthesizer.emi_info.3").asWidget())));

    public static GTRecipeType SCULK_VAT_RECIPES = GTRecipeTypes
            .register("sculk_vat", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 0, 3, 1)
            .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW_MULTIPLE)
                    .addRecipeUIModifier((recipe, widget) -> {
                        widget.textComponents.child(Text.lang("emi_info.monilabs.multiblock.sculk_vat.0").asWidget())
                                .child(Text.lang("emi_info.monilabs.multiblock.sculk_vat.1").asWidget())
                                .child(Text.lang("emi_info.monilabs.multiblock.sculk_vat.2").asWidget())
                                .child(Text.dynamic(() -> {
                                    if (!recipe.data.contains("minimumXp") || !recipe.data.contains("maximumXp"))
                                        return Component.empty();
                                    int minimumXp = recipe.data.getInt("minimumXp");
                                    int maximumXp = recipe.data.getInt("maximumXp");
                                    return Component.translatable("emi_info.monilabs.multiblock.sculk_vat.3", minimumXp,
                                            maximumXp);
                                }).asWidget()
                                        .setEnabledIf(w -> recipe.data.contains("minimumXp") &&
                                                recipe.data.contains("maximumXp")));

                    }))
            .setSound(GTSoundEntries.CHEMICAL);

    public static GTRecipeType SIMULATION_SUPERCOMPUTER_RECIPES = GTRecipeTypes
            .register("simulation_supercomputer", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 2, 0, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 1, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(MoniGuiTextures.PROGRESS_BAR_SIMULATION))
            .setSound(GTSoundEntries.ASSEMBLER);

    public static GTRecipeType LOOT_SUPERFABRICATOR_RECIPES = GTRecipeTypes
            .register("loot_superfabricator", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 1, 0, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 1, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(MoniGuiTextures.PROGRESS_BAR_SIMULATION))
            .setSound(GTSoundEntries.COMPUTATION);

    public static GTRecipeType ACTUALIZATION_CHAMBER_RECIPES = GTRecipeTypes
            .register("actualization_chamber", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 20, 0, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 1, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.COOLING);

    public static GTRecipeType NAQUADAH_REACTOR_RECIPES = GTRecipeTypes
            .register("naquadah_reactor", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.OUT)
            .setMaxIOSize(2, 2, 0, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 1, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.ARC);

    public static GTRecipeType LARGE_NAQUADAH_REACTOR_RECIPES = GTRecipeTypes
            .register("large_naquadah_reactor", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.OUT)
            .setMaxIOSize(0, 0, 1, 0)
            .UI(builder -> builder.setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.ARC);

    public static GTRecipeType NAQUADAH_REFINERY_RECIPES = GTRecipeTypes
            .register("naquadah_refinery", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(6, 0, 5, 1)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 5, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.COOLING);

    public static GTRecipeType GREENHOUSE_RECIPES = GTRecipeTypes
            .register("greenhouse", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(3, 6, 1, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 2, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.TURBINE);

    public static GTRecipeType QUINTESSENCE_INFUSER_RECIPES = GTRecipeTypes
            .register("quintessence_infuser", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(2, 2, 1, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 1, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(MoniGuiTextures.PROGRESS_BAR_XP))
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static GTRecipeType ROCK_CYCLE_SIMULATOR_RECIPES = GTRecipeTypes
            .register("rock_cycle_simulator", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 1, 0, 0)
            .UI(builder -> builder.setItemSlotOverlay(IO.IN, 0, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_MACERATE))
            .setSound(GTSoundEntries.MINER);

    public static GTRecipeType DISCHARGER_RECIPES = GTRecipeTypes
            .register("discharger", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(9, 1, 1, 0)
            .UI(builder -> builder.setItemSlotsOverlay(IO.IN, 0, 8, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.ELECTROLYZER);

    public static GTRecipeType ANTIMATTER_MANIPULATION_RECIPES = GTRecipeTypes
            .register("antimatter_manipulation", GTRecipeTypes.MULTIBLOCK)
            .setEUIO(IO.IN)
            .setMaxIOSize(1, 1, 3, 1)
            .UI(builder -> builder.setItemSlotOverlay(IO.IN, 0, GTGuiTextures.ARROW_INPUT_OVERLAY)
                    .setProgressBar(GTGuiTextures.PROGRESS_ARROW))
            .setSound(GTSoundEntries.CHEMICAL);

    public static void init() {}
}
