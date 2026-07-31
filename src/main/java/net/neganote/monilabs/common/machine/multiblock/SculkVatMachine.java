package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.BlockEntityCreationInfo;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.notifiable.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.recipe.RecipeLogic;
import com.gregtechceu.gtceu.api.multiblock.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.sync_system.annotations.RerenderOnChanged;
import com.gregtechceu.gtceu.api.sync_system.annotations.SaveField;
import com.gregtechceu.gtceu.common.machine.trait.multiblock.MultiblockFluidRendererTrait;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.machine.part.SculkExperienceDrainingHatchPartMachine;

import brachy.modularui.api.drawable.Text;
import brachy.modularui.api.widget.IWidget;
import brachy.modularui.value.sync.IntSyncValue;
import brachy.modularui.value.sync.PanelSyncManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SculkVatMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler xpHatchSubscription;

    @SaveField
    @Getter
    private int xpBuffer = 0;

    @SaveField
    @Getter
    private int timer = 0;

    @Getter
    @Setter
    @RerenderOnChanged
    private @NotNull Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    @Getter
    @SaveField
    private GTRecipe lastSavedRecipe = null;

    public static int XP_BUFFER_MAX = FluidType.BUCKET_VOLUME << GTValues.ZPM;

    public SculkVatMachine(BlockEntityCreationInfo info) {
        super(info);
        attachTrait(new MultiblockFluidRendererTrait(this::getOffsets));
        this.xpHatchSubscription = new ConditionalSubscriptionHandler(this, this::xpHatchTick, () -> true);
    }

    private void xpHatchTick() {
        if (timer == 0) {
            if (xpBuffer != 0) {
                xpBuffer -= Math.max(xpBuffer >> 6, 1);
            }
            int stored = 0;
            if (isFormed()) {
                var array = getParts().stream()
                        .filter(SculkExperienceDrainingHatchPartMachine.class::isInstance)
                        .map(SculkExperienceDrainingHatchPartMachine.class::cast)
                        .toArray(SculkExperienceDrainingHatchPartMachine[]::new);

                if (array.length == 1) {
                    var xpHatch = array[0];

                    var xpTank = (NotifiableFluidTank) xpHatch.getRecipeHandlers().get(0)
                            .getCapability(FluidRecipeCapability.CAP).get(0);
                    if (!xpTank.isEmpty()) {
                        stored = ((FluidStack) xpTank.getContents().get(0)).getAmount();
                    }

                    xpBuffer = Math.min(XP_BUFFER_MAX, xpBuffer + stored);
                    xpTank.setFluidInTank(0, FluidStack.EMPTY);
                }
            }
        }
        timer = (timer + 1) % 20;
    }

    @Override
    public void formStructure(@NotNull String substructureName) {
        super.formStructure(substructureName);
        xpHatchSubscription.updateSubscription();
    }

    @Override
    public void invalidateStructure(String substructureName) {
        super.invalidateStructure(substructureName);
        xpHatchSubscription.updateSubscription();
        timer = 0;
        lastSavedRecipe = null;
    }

    public @NotNull Set<BlockPos> getOffsets() {
        Direction up = RelativeDirection.UP.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction back = getFrontFacing().getOpposite();
        Direction right = RelativeDirection.RIGHT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction left = RelativeDirection.LEFT.getRelativeFacing(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos pos = getBlockPos();

        ObjectOpenHashSet<BlockPos> offsets = new ObjectOpenHashSet<>();

        BlockPos loopPosFront = pos.relative(up).relative(back);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosFront.relative(up, i).subtract(pos));
        }

        BlockPos loopPosBack = loopPosFront.relative(back, 2);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosBack.relative(up, i).subtract(pos));
        }

        BlockPos loopPosLeft = loopPosFront.relative(back).relative(left);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosLeft.relative(up, i).subtract(pos));
        }

        BlockPos loopPosRight = loopPosFront.relative(back).relative(right);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosRight.relative(up, i).subtract(pos));
        }

        return offsets;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) {
            return false;
        }
        if (recipe == null) {
            return false;
        }

        var data = recipe.data;
        if (!data.contains("minimumXp") || !data.contains("maximumXp")) {
            lastSavedRecipe = recipe;
            return true;
        }

        int minimumXp = data.getInt("minimumXp");
        int maximumXp = data.getInt("maximumXp");

        if (xpBuffer >= minimumXp && xpBuffer <= maximumXp) {
            lastSavedRecipe = recipe;
            return true;
        } else {
            RecipeLogic.putFailureReason(this, recipe, Component.translatable("monilabs.failure_reason.improper_xp"));
            return false;
        }
    }

    @Override
    public boolean onWorking() {
        var recipe = getRecipeLogic().getLastRecipe();
        if (recipe != null && recipe.data.contains("minimumXp") && recipe.data.contains("maximumXp")) {
            int minimumXp = recipe.data.getInt("minimumXp");
            int maximumXp = recipe.data.getInt("maximumXp");
            if (!(xpBuffer >= minimumXp && xpBuffer <= maximumXp)) {
                if (recipeLogic.getProgress() > 1) {
                    recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - 2));
                }
            }
        }
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
    }

    @Override
    public List<IWidget> getWidgetsForDisplay(PanelSyncManager syncManager) {
        IntSyncValue xpSyncValue = new IntSyncValue(this::getXpBuffer);
        syncManager.syncValue("xpBuffer", xpSyncValue);
        var list = super.getWidgetsForDisplay(syncManager);
        list.add(Text.dynamic(() -> Component.translatable("sculk_vat.monilabs.current_xp_buffer",
                xpSyncValue.getIntValue(), XP_BUFFER_MAX)).asWidget());
        return list;
    }
}
