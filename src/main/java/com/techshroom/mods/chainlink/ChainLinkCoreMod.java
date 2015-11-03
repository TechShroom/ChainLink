package com.techshroom.mods.chainlink;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IEnvironmentTokenProvider;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;

@IFMLLoadingPlugin.MCVersion("1.8")
public class ChainLinkCoreMod implements IFMLLoadingPlugin {

    public static final class TokenProvider
            implements IEnvironmentTokenProvider {

        @Override
        public int getPriority() {
            return IEnvironmentTokenProvider.DEFAULT_PRIORITY;
        }

        @Override
        public Integer getToken(String token, MixinEnvironment env) {
            if ("FORGE".equals(token)) {
                return Integer.valueOf(ForgeVersion.getBuildVersion());
            } else if ("FML".equals(token)) {
                String fmlVersion = Loader.instance().getFMLVersionString();
                int build = Integer.parseInt(
                        fmlVersion.substring(fmlVersion.lastIndexOf('.') + 1));
                return Integer.valueOf(build);
            }
            return null;
        }

    }

    public ChainLinkCoreMod() {
        // Let's get this party started
        MixinBootstrap.init();

        // Add default mixins
        MixinEnvironment.getDefaultEnvironment()
                .addConfiguration("mixins.chainlink.json")
                .registerTokenProviderClass(
                        "com.techshroom.mods.chainlink.ChainLinkCoreMod$TokenProvider");

        // Transformer exclusions
        Launch.classLoader.addTransformerExclusion("org.apache.commons.lang3.");
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "com.techshroom.mods.ChainLink";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if ((Boolean) data.get("runtimeDeobfuscationEnabled")) {
            MixinEnvironment.getDefaultEnvironment().registerErrorHandlerClass(
                    "com.techshroom.mods.chainlink.mixin.MixinErrorHandler");
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}