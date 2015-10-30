package com.techshroom.mods.chainlink.block;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.value.AutoValue;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@AutoValue
public abstract class BlockPos {

    private static final int MIN_EDGE = -30_000_000;
    private static final int MAX_EDGE = +30_000_000;
    private static final int MIN_HEIGHT = 0;
    private static final int MAX_HEIGHT = 256;

    private static final Map<String, BlockPos> CACHE = new HashMap<>();

    private static final BlockPos getFromCache(int x, int y, int z,
            World world) {
        // TODO optimize?
        String cacheKey = x + "|" + y + "|" + z + "|"
                + world.getWorldInfo().getWorldName();
        return CACHE.computeIfAbsent(cacheKey,
                k -> new AutoValue_BlockPos(x, y, z, world));
    }

    public static final BlockPos of(int x, int y, int z, World world) {
        // limit BlockPos to sensible locations
        checkArgument(MIN_EDGE <= x && x <= MAX_EDGE,
                "x=%s is out of reasonable bounds", x);
        checkArgument(MIN_HEIGHT <= y && y <= MAX_HEIGHT,
                "y=%s is out of reasonable bounds", y);
        checkArgument(MIN_EDGE <= z && z <= MAX_EDGE,
                "z=%s is out of reasonable bounds", z);
        checkNotNull(world, "You fool.");
        return getFromCache(x, y, z, world);
    }

    BlockPos() {
    }

    public abstract int x();

    public abstract int y();

    public abstract int z();

    public abstract World world();

    public final BlockPos xpp() {
        return of(x() + 1, y(), z(), world());
    }

    public final BlockPos ypp() {
        return of(x(), y() + 1, z(), world());
    }

    public final BlockPos zpp() {
        return of(x(), y(), z() + 1, world());
    }

    public final BlockPos xmm() {
        return of(x() - 1, y(), z(), world());
    }

    public final BlockPos ymm() {
        return of(x(), y() - 1, z(), world());
    }

    public final BlockPos zmm() {
        return of(x(), y(), z() - 1, world());
    }

    public final BlockPos move(EnumFacing direction) {
        return of(x() + direction.getFrontOffsetX(),
                y() + direction.getFrontOffsetY(),
                z() + direction.getFrontOffsetZ(), world());
    }

    public final Block getBlock() {
        return world().getBlock(x(), y(), z());
    }

    public final TileEntity getTileEntity() {
        return world().getTileEntity(x(), y(), z());
    }

}
