package com.techshroom.mods.chainlink.block;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.value.AutoValue;
import com.techshroom.mods.chainlink.te.TileEntityReplaceMe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockChainLink extends BlockContainer {

    /**
     * Data on blocks we're mirroring. Includes type, position, etc.
     */
    @AutoValue
    static abstract class MirrorData {

        private static final MirrorData of(BlockPos pos) {
            return new AutoValue_BlockChainLink_MirrorData(pos.getBlock(), pos);
        }

        abstract Block getBlockType();

        abstract BlockPos getPosition();

    }

    private static final Map<BlockPos, MirrorData> mirrorDataMap =
            new HashMap<>();

    public BlockChainLink() {
        super(Material.air);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        // ayyyyyyy
        return new TileEntityReplaceMe(meta);
    }

    public void copyBlock(BlockPos myPos, BlockPos targetPos) {
        MirrorData data = MirrorData.of(targetPos);
        mirrorDataMap.put(targetPos, data);
    }
    
    private boolean isMirroring(BlockPos pos) {
        return mirrorDataMap.containsKey(pos);
    }

}
