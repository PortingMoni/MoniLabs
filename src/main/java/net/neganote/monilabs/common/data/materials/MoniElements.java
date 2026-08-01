package net.neganote.monilabs.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.common.data.GTElements;

import net.neganote.monilabs.MoniLabs;

public class MoniElements {

    public static final Element CrystalMatrix = GTElements.createAndRegister(MoniLabs.id("crystal_matrix"), 6, 6, -1,
            null, "Crystal Matrix", "C☆",
            false);
    public static final Element SculkBioalloy = GTElements.createAndRegister(MoniLabs.id("sculk_bioalloy"), -1, 481, -1,
            null, "Sculk Bioalloy", "ᛋ**",
            false);
    public static final Element Eltz = GTElements.createAndRegister(MoniLabs.id("eltz"), 15, 15, -1, null, "Eltz", "Ez",
            false);
    public static final Element TranscendentalMatrix = GTElements.createAndRegister(
            MoniLabs.id("transcendental_matrix"), 6, 32, -1, null,
            "Transcendental Matrix", "ᛝ",
            false);

    public static void init() {}
}
