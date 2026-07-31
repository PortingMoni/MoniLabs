package net.neganote.monilabs.integration.jade.provider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class PrismaticColorBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof PrismaticCrucibleMachine) {
            CompoundTag data = blockAccessor.getServerData();
            if (data.contains("currentColor")) {
                var colorKey = data.getString("currentColor");
                iTooltip.add(Component.translatable("monilabs.prismatic.current_color",
                        Component.translatable(colorKey)));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MoniLabs.id("color_info");
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof PrismaticCrucibleMachine machine) {
            compoundTag.putString("currentColor", machine.getColorState().nameKey);
        }
    }
}
