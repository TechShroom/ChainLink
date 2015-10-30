package com.techshroom.mods.chainlink.util;

import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.BOTTOM;
import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.EAST;
import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.NORTH;
import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.SOUTH;
import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.TOP;
import static com.techshroom.mods.chainlink.util.ChainUtil.SideConstants.WEST;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.techshroom.mods.chainlink.ChainLink;
import com.techshroom.mods.chainlink.ChainLinkKeys;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class ChainUtil {

    @SideOnly(Side.CLIENT)
    public static final class Client {

        public static boolean buttonIsPressed(int id, GuiButton check) {
            return check.enabled && check.id == id;
        }

        private Client() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class IBS {

        private static class DistSort extends BlockSourceImpl
                implements Comparable<DistSort> {

            private int dSq = 0;

            public DistSort(World par1World, int par2, int par3, int par4,
                    IBlockSource from) {
                super(par1World, par2, par3, par4);
                this.dSq = distSq(this, from);
            }

            @Override
            public int compareTo(DistSort o) {
                if (IBS.equal(o, this)) {
                    return 0;
                }
                if (this.dSq > o.dSq) {
                    return 1;
                }
                return -1;
            }

        }

        public static Block block(IBlockSource ibs) {
            if (ibs == null) {
                return null;
            }
            return ibs.getWorld().getBlock(ibs.getXInt(), ibs.getYInt(),
                    ibs.getZInt());
        }

        public static String
                collectionAsString(Collection<? extends IBlockSource> c) {
            List<String> out = new ArrayList<String>(c.size());
            Iterator<? extends IBlockSource> i = c.iterator();
            while (i.hasNext()) {
                out.add(string(i.next()));
            }
            return out.toString();
        }

        @SuppressWarnings("unchecked")
        private static <T extends IBlockSource> T constr(Class<T> ibs, World w,
                int x, int y, int z) {
            try {
                Class<? extends IBlockSource> ibsClass = ibs;
                if (override != null) {
                    ibsClass = override;
                }
                Constructor<? extends IBlockSource> ibsConstr =
                        constrCache.get(ibsClass);
                if (ibsConstr == null) {
                    try {
                        ibsConstr = ibsClass.getConstructor(World.class,
                                int.class, int.class, int.class);
                        ibsConstr.setAccessible(true);
                    } catch (SecurityException e) {
                        System.err.println("[techshroom-util] "
                                + "SecurityException caught, falling back: "
                                + e.getMessage());
                    } catch (NoSuchMethodException e) {
                        System.err.println("[techshroom-util] " + "Class "
                                + ibsClass + " does not expose a constructor"
                                + " with the parameters [World, int, int, int]!"
                                + " Falling back to BlockSourceImpl.");
                    }
                    if (ibsConstr == null) {
                        ibsConstr = constrCache.get(BlockSourceImpl.class);
                    }
                }
                constrCache.put(ibsClass, ibsConstr);
                try {
                    return (T) ibsConstr.newInstance(w, x, y, z);
                } catch (IllegalArgumentException e) {
                    throw e;
                } catch (InstantiationException e) {
                    throw e;
                } catch (IllegalAccessException e) {
                    throw e;
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            } catch (RuntimeException re) {
                throw re;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        public static boolean contains(List<? extends IBlockSource> list,
                IBlockSource check) {
            return IBS.indexOf(list, check) != -1;
        }

        public static int distSq(IBlockSource o1, IBlockSource o2) {
            int xo = o1.getXInt(), yo = o1.getYInt(), zo = o1.getZInt(),
                    xt = o2.getXInt(), yt = o2.getYInt(), zt = o2.getZInt();
            xo = xt - xo;
            yo = yt - yo;
            zo = zt - zo;
            int distSq1 = xo * xo + yo * yo + zo * zo;
            return distSq1;
        }

        public static boolean equal(IBlockSource ibs1, IBlockSource ibs2) {
            return ibs1 != null && ibs2 != null
                    && (ibs1 == ibs2 || (ibs1.getWorld() == ibs2.getWorld()
                            && ibs1.getXInt() == ibs2.getXInt()
                            && ibs1.getYInt() == ibs2.getYInt()
                            && ibs1.getZInt() == ibs2.getZInt()));
        }

        public static int indexOf(List<? extends IBlockSource> list,
                IBlockSource check) {
            if (list != null) {
                int index = 0;
                for (IBlockSource c : list) {
                    if (equal(check, c)) {
                        return index;
                    }
                    index++;
                }
            }
            return -1;
        }

        public static <T extends IBlockSource> IBlockSource[] neighbors(T ibs) {
            IBlockSource[] n = new IBlockSource[6];
            Class<? extends IBlockSource> c = ibs.getClass();
            for (int i = 0; i < n.length; i++) {
                n[i] = constr(c, ibs.getWorld(),
                        ibs.getXInt() + Facing.offsetsXForSide[i],
                        ibs.getYInt() + Facing.offsetsYForSide[i],
                        ibs.getZInt() + Facing.offsetsZForSide[i]);
            }
            return n;
        }

        public static void setOverride(Class<? extends IBlockSource> c) {
            override = c;
        }

        @SuppressWarnings("unchecked")
        public static <T extends IBlockSource> List<T>
                sortByDistFrom(IBlockSource loc, List<T> list) {
            List<DistSort> sort = new ArrayList<DistSort>();
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                DistSort nt = new DistSort(t.getWorld(), t.getXInt(),
                        t.getYInt(), t.getZInt(), loc);
                sort.add(nt);
            }
            Collections.sort(sort);
            for (int i = 0; i < sort.size(); i++) {
                T t = list.get(i);
                DistSort d = sort.get(i);
                list.set(i, (T) constr(t.getClass(), d.getWorld(), d.getXInt(),
                        d.getYInt(), d.getZInt()));
            }
            return list;
        }

        public static String string(IBlockSource ibs) {
            if (ibs == null) {
                return String.valueOf(null);
            }
            return ibs.getClass().getSimpleName() + "["
                    + String.format("world=%s,x=%s,y=%s,z=%s",
                            ibs.getWorld().getWorldInfo().getWorldName(),
                            ibs.getXInt(), ibs.getYInt(), ibs.getZInt())
                    + "]";
        }

        public static void unsetOverride() {
            override = null;
        }

        private static Map<Class<? extends IBlockSource>, Constructor<? extends IBlockSource>> constrCache =
                new HashMap<Class<? extends IBlockSource>, Constructor<? extends IBlockSource>>();

        static {
            Constructor<BlockSourceImpl> constr =
                    cast(BlockSourceImpl.class.getDeclaredConstructors()[0]);
            constrCache.put(BlockSourceImpl.class, constr);
            constrCache.put(IBlockSource.class, constr);
        }

        private static Class<? extends IBlockSource> override = null;

        private IBS() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class MetadataConstants {

        public static final int UPDATE = 1, SEND = 2, DONT_RE_RENDER = 2;
        public static final int UPDATE_AND_SEND = UPDATE | SEND;
        public static final int SEND_AND_DONT_RE_RENDER = SEND | DONT_RE_RENDER;
        public static final int UPDATE_AND_DONT_RE_RENDER =
                UPDATE | DONT_RE_RENDER;
        public static final int UPDATE_SEND_AND_DONT_RE_RENDER =
                UPDATE | SEND | DONT_RE_RENDER;

        private MetadataConstants() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class SideConstants {

        public static final int BOTTOM = EnumFacing.DOWN.ordinal();
        public static final int TOP = EnumFacing.UP.ordinal();
        public static final int NORTH = EnumFacing.NORTH.ordinal();
        public static final int SOUTH = EnumFacing.SOUTH.ordinal();
        public static final int WEST = EnumFacing.WEST.ordinal();
        public static final int EAST = EnumFacing.EAST.ordinal();

        private SideConstants() {
            throw new AssertionError("Nope.");
        }
    }

    public static final class Time {

        public static int minutesAsSeconds(int minutes) {
            return minutes * 60;
        }

        public static int minutesAsTicks(int minutes) {
            return secondsAsTicks(minutesAsSeconds(minutes));
        }

        public static int secondsAsTicks(int seconds) {
            return seconds * 20;
        }

        private Time() {
            throw new AssertionError("Nope.");
        }
    }

    public static String address(String id, String object) {
        return id + ":" + object;
    }

    public static String addressMod(String object) {
        return address(ChainLink.ID, object);
    }

    public static int clockwise(int side) {
        return CLOCKWISE_TABLE[side];
    }

    public static int counterClockwise(int side) {
        return COUNTERCLOCKWISE_TABLE[side];
    }

    public static void drawBackground(GuiScreen gui, int xoff, int yoff, int u,
            int v, int w, int h) {
        gui.drawTexturedModalRect(xoff + u, yoff + v, u, v, w, h);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    public static int getMetadataForBlock(int x, int y, int z,
            EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F
                && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + 1.82D - (double) entity.yOffset;

            if (d0 - (double) y > 2.0D) {
                return 1;
            }

            if ((double) y - d0 > 0.0D) {
                return 0;
            }
        }

        int l = MathHelper.floor_double(
                (double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    /**
     * Gets the side meta, but only for left/right/front/back, not up/down
     */
    public static int getMetadataForBlockOnlySided(EntityLivingBase base) {
        int l = MathHelper.floor_double(
                (double) (base.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) {
            return 2;
        }

        if (l == 1) {
            return 5;
        }

        if (l == 2) {
            return 3;
        }

        if (l == 3) {
            return 4;
        }
        return 2;
    }

    public static void reverse(Object[] o) {
        Object[] copy = o.clone();
        for (int i = copy.length - 1; i >= 0; i--) {
            o[i] = copy[copy.length - i - 1];
        }
    }

    public static void throwing(Throwable t) {
        ChainLink.STORE.get(ChainLinkKeys.LOGGER).get().throwing(t);
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new RuntimeException(t);
        }
    }

    public static boolean isClient(World w) {
        return w == null
                ? FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT
                : w.isRemote;
    }

    public static void placeTileEntityCopy(TileEntity original, Block block,
            World w, int x, int y, int z) {
        TileEntity copy = null;
        if (copy == null) {
            copy = block.createTileEntity(w, w.getBlockMetadata(x, y, z));
            if (original.getClass() != copy.getClass()) {
                throw new IllegalArgumentException(
                        "Transfers only valid between same class "
                                + String.format("(%s != %s)",
                                        original.getClass(), copy.getClass()));
            }
        }
        NBTTagCompound copyTag = new NBTTagCompound();
        original.writeToNBT(copyTag);
        copyTag.setInteger("x", x);
        copyTag.setInteger("y", y);
        copyTag.setInteger("z", z);
        copy.readFromNBT(copyTag);
        w.setBlock(x, y, z, block);
        w.setTileEntity(x, y, z, copy);
    }

    private static final int[] COUNTERCLOCKWISE_TABLE, CLOCKWISE_TABLE;

    static {
        COUNTERCLOCKWISE_TABLE = new int[6];
        CLOCKWISE_TABLE = new int[6];
        CLOCKWISE_TABLE[TOP] = COUNTERCLOCKWISE_TABLE[TOP] = TOP;
        CLOCKWISE_TABLE[BOTTOM] = COUNTERCLOCKWISE_TABLE[BOTTOM] = BOTTOM;
        CLOCKWISE_TABLE[NORTH] = EAST;
        CLOCKWISE_TABLE[EAST] = SOUTH;
        CLOCKWISE_TABLE[SOUTH] = WEST;
        CLOCKWISE_TABLE[WEST] = NORTH;

        COUNTERCLOCKWISE_TABLE[NORTH] = WEST;
        COUNTERCLOCKWISE_TABLE[WEST] = SOUTH;
        COUNTERCLOCKWISE_TABLE[SOUTH] = EAST;
        COUNTERCLOCKWISE_TABLE[EAST] = NORTH;
    }

    private ChainUtil() {
    }
}
