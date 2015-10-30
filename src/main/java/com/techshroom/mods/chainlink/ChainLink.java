package com.techshroom.mods.chainlink;

import com.techshroom.mods.chainlink.util.Storage;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(version = ChainLink.VERSION, modid = ChainLink.ID, name = ChainLink.NAME)
public final class ChainLink {

    public static final String VERSION = "@VERSION@";
    public static final String ID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final Storage STORE = new Storage();
    private static ChainLink INSTANCE;

    public static ChainLink instance() {
        return INSTANCE;
    }

    {
        INSTANCE = this;
    }
    
    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        STORE.put(ChainLinkKeys.LOGGER, e.getModLog());
    }

}
