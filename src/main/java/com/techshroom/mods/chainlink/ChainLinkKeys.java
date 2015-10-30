package com.techshroom.mods.chainlink;

import org.apache.logging.log4j.Logger;

import com.techshroom.mods.chainlink.util.Storage.IntKey;
import com.techshroom.mods.chainlink.util.Storage.Key;

/**
 * Storage keys.
 */
public final class ChainLinkKeys {

    public static final Key<Logger> LOGGER = Key.Named.create("logger");
    public static final IntKey CHAIN_LINK_BLOCK_RENDER_TYPE =
            IntKey.Named.create("clblockRenderId");

}
