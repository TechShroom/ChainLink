package com.techshroom.mods.chainlink.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.auto.value.AutoValue;
import com.techshroom.mods.chainlink.ChainLink;
import com.techshroom.mods.chainlink.ChainLinkKeys;
import com.techshroom.mods.chainlink.te.TileEntityReplaceMe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

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

    /**
     * N.B. Also performs a sanity check that the mirror is still valid.
     */
    public static boolean isMirroring(BlockPos pos) {
        MirrorData data = mirrorDataMap.get(pos);
        if (data.getPosition().getBlock() != data.getBlockType()) {
            mirrorDataMap.remove(pos);
            return false;
        }
        return true;
    }

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

    // BEGIN THE MIRRORING PROCESS.

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            pos.getBlock().onBlockAdded(world, x, y, z);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b,
            int meta) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            pos.getBlock().breakBlock(world, x, y, z, b, meta);
        }
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z,
            int eventId, int eventData) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().onBlockEventReceived(world, x, y, z, eventId,
                    eventData);
        }
        return super.onBlockEventReceived(world, x, y, z, eventId, eventData);
    }

    @Override
    public boolean renderAsNormalBlock() {
        // We render whatever block we're targeting
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getBlocksMovement(world, x, y, z);
        }
        return super.getBlocksMovement(world, x, y, z);
    }

    @Override
    public int getRenderType() {
        return ChainLink.STORE
                .getOrFail(ChainLinkKeys.CHAIN_LINK_BLOCK_RENDER_TYPE);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getBlockHardness(world, x, y, z);
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y,
            int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getMixedBrightnessForBlock(world, x, y, z);
        }
        return super.getMixedBrightnessForBlock(world, x, y, z);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z,
            int side) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().shouldSideBeRendered(world, x, y, z, side);
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean isBlockSolid(IBlockAccess world, int x, int y, int z,
            int side) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().isBlockSolid(world, x, y, z, side);
        }
        return super.isBlockSolid(world, x, y, z, side);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getIcon(world, x, y, z, side);
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z,
            AxisAlignedBB mask, @SuppressWarnings("rawtypes") List list,
            Entity collider) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            pos.getBlock().addCollisionBoxesToList(world, x, y, z, mask, list,
                    collider);
        } else {
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
            int y, int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getCollisionBoundingBoxFromPool(world, x, y,
                    z);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
            int y, int z) {
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            return pos.getBlock().getSelectedBoundingBoxFromPool(world, x, y,
                    z);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        // We explicitly don't override this because we are a view, not a real
        // block.
        super.updateTick(world, x, y, z, random);
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z,
            Random random) {
        // so random.
        BlockPos pos = BlockPos.of(x, y, z, world);
        if (isMirroring(pos)) {
            pos.getBlock().randomDisplayTick(world, x, y, z, random);
        } else {
            super.randomDisplayTick(world, x, y, z, random);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z,
            int meta) {
        // TODO Auto-generated method stub
        super.onBlockDestroyedByPlayer(world, x, y, z, meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z,
            Block neighbor) {
        // TODO Auto-generated method stub
        super.onNeighborBlockChange(world, x, y, z, neighbor);
    }

    @Override
    public int tickRate(World world) {
        // TODO Auto-generated method stub
        return super.tickRate(world);
    }

    @Override
    public int quantityDropped(Random random) {
        // TODO Auto-generated method stub
        return super.quantityDropped(random);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        // TODO Auto-generated method stub
        return super.getItemDropped(meta, random, fortune);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player,
            World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z,
            int meta, float chance, int fortune) {
        // TODO Auto-generated method stub
        super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
    }

    @Override
    protected void dropBlockAsItem(World world, int x, int y, int z,
            ItemStack itemIn) {
        // TODO Auto-generated method stub
        super.dropBlockAsItem(world, x, y, z, itemIn);
    }

    @Override
    public void dropXpOnBlockBreak(World world, int x, int y, int z,
            int amount) {
        // TODO Auto-generated method stub
        super.dropXpOnBlockBreak(world, x, y, z, amount);
    }

    @Override
    public int damageDropped(int meta) {
        // TODO Auto-generated method stub
        return super.damageDropped(meta);
    }

    @Override
    public float getExplosionResistance(Entity exploder) {
        // TODO Auto-generated method stub
        return super.getExplosionResistance(exploder);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y,
            int z, Vec3 startVec, Vec3 endVec) {
        // TODO Auto-generated method stub
        return super.collisionRayTrace(world, x, y, z, startVec, endVec);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z,
            Explosion explosionIn) {
        // TODO Auto-generated method stub
        super.onBlockDestroyedByExplosion(world, x, y, z, explosionIn);
    }

    @Override
    public boolean canReplace(World world, int x, int y, int z, int side,
            ItemStack itemIn) {
        // TODO Auto-generated method stub
        return super.canReplace(world, x, y, z, side, itemIn);
    }

    @Override
    public int getRenderBlockPass() {
        // TODO Auto-generated method stub
        return super.getRenderBlockPass();
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.canPlaceBlockOnSide(world, x, y, z, side);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int side, float subX, float subY, float subZ) {
        // TODO Auto-generated method stub
        return super.onBlockActivated(world, x, y, z, player, side, subX, subY,
                subZ);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z,
            Entity entityIn) {
        // TODO Auto-generated method stub
        super.onEntityWalking(world, x, y, z, entityIn);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side,
            float subX, float subY, float subZ, int meta) {
        // TODO Auto-generated method stub
        return super.onBlockPlaced(world, x, y, z, side, subX, subY, subZ,
                meta);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z,
            EntityPlayer player) {
        // TODO Auto-generated method stub
        super.onBlockClicked(world, x, y, z, player);
    }

    @Override
    public void velocityToAddToEntity(World world, int x, int y, int z,
            Entity entityIn, Vec3 velocity) {
        // TODO Auto-generated method stub
        super.velocityToAddToEntity(world, x, y, z, entityIn, velocity);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
            int z) {
        // TODO Auto-generated method stub
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public int getBlockColor() {
        // TODO Auto-generated method stub
        return super.getBlockColor();
    }

    @Override
    public int getRenderColor(int meta) {
        // TODO Auto-generated method stub
        return super.getRenderColor(meta);
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.colorMultiplier(world, x, y, z);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.isProvidingWeakPower(world, x, y, z, side);
    }

    @Override
    public boolean canProvidePower() {
        // TODO Auto-generated method stub
        return super.canProvidePower();
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z,
            Entity entityIn) {
        // TODO Auto-generated method stub
        super.onEntityCollidedWithBlock(world, x, y, z, entityIn);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.isProvidingStrongPower(world, x, y, z, side);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        // TODO Auto-generated method stub
        super.setBlockBoundsForItemRender();
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y,
            int z, int meta) {
        // TODO Auto-generated method stub
        super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    protected boolean canSilkHarvest() {
        // TODO Auto-generated method stub
        return super.canSilkHarvest();
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        // TODO Auto-generated method stub
        return super.createStackedBlock(meta);
    }

    @Override
    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        // TODO Auto-generated method stub
        return super.quantityDroppedWithBonus(maxBonus, random);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.canBlockStay(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
            EntityLivingBase placer, ItemStack itemIn) {
        // TODO Auto-generated method stub
        super.onBlockPlacedBy(world, x, y, z, placer, itemIn);
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        // TODO Auto-generated method stub
        super.onPostBlockPlaced(world, x, y, z, meta);
    }

    @Override
    public Block setBlockName(String name) {
        // TODO Auto-generated method stub
        return super.setBlockName(name);
    }

    @Override
    public String getLocalizedName() {
        // TODO Auto-generated method stub
        return super.getLocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        // TODO Auto-generated method stub
        return super.getUnlocalizedName();
    }

    @Override
    public boolean getEnableStats() {
        // TODO Auto-generated method stub
        return super.getEnableStats();
    }

    @Override
    protected Block disableStats() {
        // TODO Auto-generated method stub
        return super.disableStats();
    }

    @Override
    public int getMobilityFlag() {
        // TODO Auto-generated method stub
        return super.getMobilityFlag();
    }

    @Override
    public float getAmbientOcclusionLightValue() {
        // TODO Auto-generated method stub
        return super.getAmbientOcclusionLightValue();
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entityIn,
            float fallDistance) {
        // TODO Auto-generated method stub
        super.onFallenUpon(world, x, y, z, entityIn, fallDistance);
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getItem(world, x, y, z);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getDamageValue(world, x, y, z);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab,
            @SuppressWarnings("rawtypes") List list) {
        // TODO Auto-generated method stub
        super.getSubBlocks(itemIn, tab, list);
    }

    @Override
    public Block setCreativeTab(CreativeTabs tab) {
        // TODO Auto-generated method stub
        return super.setCreativeTab(tab);
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta,
            EntityPlayer player) {
        // TODO Auto-generated method stub
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn() {
        // TODO Auto-generated method stub
        return super.getCreativeTabToDisplayOn();
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        // TODO Auto-generated method stub
        super.onBlockPreDestroy(world, x, y, z, meta);
    }

    @Override
    public void fillWithRain(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        super.fillWithRain(world, x, y, z);
    }

    @Override
    public boolean isFlowerPot() {
        // TODO Auto-generated method stub
        return super.isFlowerPot();
    }

    @Override
    public boolean func_149698_L() {
        // TODO Auto-generated method stub
        return super.func_149698_L();
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        // TODO Auto-generated method stub
        return super.canDropFromExplosion(explosionIn);
    }

    @Override
    public boolean isAssociatedBlock(Block other) {
        // TODO Auto-generated method stub
        return super.isAssociatedBlock(other);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        // TODO Auto-generated method stub
        return super.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.getComparatorInputOverride(world, x, y, z, side);
    }

    @Override
    public Block setBlockTextureName(String textureName) {
        // TODO Auto-generated method stub
        return super.setBlockTextureName(textureName);
    }

    @Override
    protected String getTextureName() {
        // TODO Auto-generated method stub
        return super.getTextureName();
    }

    @Override
    public IIcon func_149735_b(int side, int meta) {
        // TODO Auto-generated method stub
        return super.func_149735_b(side, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        // TODO Auto-generated method stub
        super.registerBlockIcons(reg);
    }

    @Override
    public String getItemIconName() {
        // TODO Auto-generated method stub
        return super.getItemIconName();
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getLightValue(world, x, y, z);
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z,
            EntityLivingBase entity) {
        // TODO Auto-generated method stub
        return super.isLadder(world, x, y, z, entity);
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isNormalCube(world, x, y, z);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z,
            ForgeDirection side) {
        // TODO Auto-generated method stub
        return super.isSideSolid(world, x, y, z, side);
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isReplaceable(world, x, y, z);
    }

    @Override
    public boolean isBurning(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isBurning(world, x, y, z);
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isAir(world, x, y, z);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        // TODO Auto-generated method stub
        return super.canHarvestBlock(player, meta);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x,
            int y, int z, boolean willHarvest) {
        // TODO Auto-generated method stub
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Deprecated
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x,
            int y, int z) {
        // TODO Auto-generated method stub
        return super.removedByPlayer(world, player, x, y, z);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z,
            ForgeDirection face) {
        // TODO Auto-generated method stub
        return super.getFlammability(world, x, y, z, face);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z,
            ForgeDirection face) {
        // TODO Auto-generated method stub
        return super.isFlammable(world, x, y, z, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z,
            ForgeDirection face) {
        // TODO Auto-generated method stub
        return super.getFireSpreadSpeed(world, x, y, z, face);
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z,
            ForgeDirection side) {
        // TODO Auto-generated method stub
        return super.isFireSource(world, x, y, z, side);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        // TODO Auto-generated method stub
        return super.hasTileEntity(metadata);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        // TODO Auto-generated method stub
        return super.createTileEntity(world, metadata);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        // TODO Auto-generated method stub
        return super.quantityDropped(meta, fortune, random);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
            int metadata, int fortune) {
        // TODO Auto-generated method stub
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x,
            int y, int z, int metadata) {
        // TODO Auto-generated method stub
        return super.canSilkHarvest(world, player, x, y, z, metadata);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world,
            int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.canCreatureSpawn(type, world, x, y, z);
    }

    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z,
            EntityLivingBase player) {
        // TODO Auto-generated method stub
        return super.isBed(world, x, y, z, player);
    }

    @Override
    public ChunkCoordinates getBedSpawnPosition(IBlockAccess world, int x,
            int y, int z, EntityPlayer player) {
        // TODO Auto-generated method stub
        return super.getBedSpawnPosition(world, x, y, z, player);
    }

    @Override
    public void setBedOccupied(IBlockAccess world, int x, int y, int z,
            EntityPlayer player, boolean occupied) {
        // TODO Auto-generated method stub
        super.setBedOccupied(world, x, y, z, player, occupied);
    }

    @Override
    public int getBedDirection(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getBedDirection(world, x, y, z);
    }

    @Override
    public boolean isBedFoot(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isBedFoot(world, x, y, z);
    }

    @Override
    public void beginLeavesDecay(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        super.beginLeavesDecay(world, x, y, z);
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.canSustainLeaves(world, x, y, z);
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isLeaves(world, x, y, z);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y,
            int z) {
        // TODO Auto-generated method stub
        return super.canBeReplacedByLeaves(world, x, y, z);
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isWood(world, x, y, z);
    }

    @Override
    public boolean isReplaceableOreGen(World world, int x, int y, int z,
            Block target) {
        // TODO Auto-generated method stub
        return super.isReplaceableOreGen(world, x, y, z, target);
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x,
            int y, int z, double explosionX, double explosionY,
            double explosionZ) {
        // TODO Auto-generated method stub
        return super.getExplosionResistance(par1Entity, world, x, y, z,
                explosionX, explosionY, explosionZ);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z,
            Explosion explosion) {
        // TODO Auto-generated method stub
        super.onBlockExploded(world, x, y, z, explosion);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.canConnectRedstone(world, x, y, z, side);
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.canPlaceTorchOnTop(world, x, y, z);
    }

    @Override
    public boolean canRenderInPass(int pass) {
        // TODO Auto-generated method stub
        return super.canRenderInPass(pass);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world,
            int x, int y, int z, EntityPlayer player) {
        // TODO Auto-generated method stub
        return super.getPickBlock(target, world, x, y, z, player);
    }

    @Deprecated
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world,
            int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public boolean isFoliage(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isFoliage(world, x, y, z);
    }

    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target,
            EffectRenderer effectRenderer) {
        // TODO Auto-generated method stub
        return super.addHitEffects(worldObj, target, effectRenderer);
    }

    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta,
            EffectRenderer effectRenderer) {
        // TODO Auto-generated method stub
        return super.addDestroyEffects(world, x, y, z, meta, effectRenderer);
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z,
            ForgeDirection direction, IPlantable plantable) {
        // TODO Auto-generated method stub
        return super.canSustainPlant(world, x, y, z, direction, plantable);
    }

    @Override
    public void onPlantGrow(World world, int x, int y, int z, int sourceX,
            int sourceY, int sourceZ) {
        // TODO Auto-generated method stub
        super.onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.isFertile(world, x, y, z);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getLightOpacity(world, x, y, z);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z,
            Entity entity) {
        // TODO Auto-generated method stub
        return super.canEntityDestroy(world, x, y, z, entity);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z,
            int beaconX, int beaconY, int beaconZ) {
        // TODO Auto-generated method stub
        return super.isBeaconBase(worldObj, x, y, z, beaconX, beaconY, beaconZ);
    }

    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z,
            ForgeDirection axis) {
        // TODO Auto-generated method stub
        return super.rotateBlock(worldObj, x, y, z, axis);
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y,
            int z) {
        // TODO Auto-generated method stub
        return super.getValidRotations(worldObj, x, y, z);
    }

    @Override
    public float getEnchantPowerBonus(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getEnchantPowerBonus(world, x, y, z);
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z,
            ForgeDirection side, int colour) {
        // TODO Auto-generated method stub
        return super.recolourBlock(world, x, y, z, side, colour);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        // TODO Auto-generated method stub
        return super.getExpDrop(world, metadata, fortune);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z,
            int tileX, int tileY, int tileZ) {
        // TODO Auto-generated method stub
        super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z,
            int side) {
        // TODO Auto-generated method stub
        return super.shouldCheckWeakPower(world, x, y, z, side);
    }

    @Override
    public boolean getWeakChanges(IBlockAccess world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return super.getWeakChanges(world, x, y, z);
    }

    @Override
    public void setHarvestLevel(String toolClass, int level) {
        // TODO Auto-generated method stub
        super.setHarvestLevel(toolClass, level);
    }

    @Override
    public void setHarvestLevel(String toolClass, int level, int metadata) {
        // TODO Auto-generated method stub
        super.setHarvestLevel(toolClass, level, metadata);
    }

    @Override
    public String getHarvestTool(int metadata) {
        // TODO Auto-generated method stub
        return super.getHarvestTool(metadata);
    }

    @Override
    public int getHarvestLevel(int metadata) {
        // TODO Auto-generated method stub
        return super.getHarvestLevel(metadata);
    }

    @Override
    public boolean isToolEffective(String type, int metadata) {
        // TODO Auto-generated method stub
        return super.isToolEffective(type, metadata);
    }

    @Override
    protected List<ItemStack> captureDrops(boolean start) {
        // TODO Auto-generated method stub
        return super.captureDrops(start);
    }

}
