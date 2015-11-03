package com.techshroom.mods.chainlink.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.ConstraintParser.Constraint;
import org.spongepowered.asm.util.ConstraintViolationException;
import org.spongepowered.asm.util.PrettyPrinter;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.ForgeVersion;

/**
 * Error handler for ChainLink mixins
 */
public class MixinErrorHandler implements IMixinErrorHandler {

    /**
     * Captain's log, stardate 68788.5
     */
    // Use @ID@ to prevent loading of ChainLink.
    private final Logger log = LogManager.getLogger("@ID@");

    @Override
    public ErrorAction onError(String targetClassName, Throwable th,
            IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR && mixin.getConfig().getMixinPackage()
                .startsWith("com.techshroom.mods.chainlink.")) {
            PrettyPrinter errorPrinter = new PrettyPrinter();

            if (th.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException ex =
                        (ConstraintViolationException) th.getCause();
                Constraint constraint = ex.getConstraint();
                if ("FORGE".equals(constraint.getToken())) {
                    errorPrinter =
                            this.forgeVersionNotValid(errorPrinter, constraint);
                } else {
                    errorPrinter = this.patchConstraintFailed(errorPrinter,
                            constraint, ex);
                }
            } else {
                errorPrinter = this.itsAllGoneHorriblyWrong(errorPrinter);
            }

            this.appendTechnicalInfo(errorPrinter, targetClassName, th, mixin)
                    .log(this.log);

            FMLCommonHandler.instance().exitJava(1, true);
        }

        return null;
    }

    private PrettyPrinter forgeVersionNotValid(PrettyPrinter errorPrinter,
            Constraint constraint) {
        String forgeVer = "@FORGE_VERSION@";
        String forgeMessage = forgeVer == null
                ? String.valueOf(constraint.getMin()) : forgeVer;

        return errorPrinter.add()
                .add("Oh dear. It seems like this version of ChainLink is not compatible with the version")
                .add("of Forge you are running.").add().hr('-').add()
                .add("A patch constraint violation was encountered whilst patching:")
                .add()
                .add("  One or more ChainLink patches could not be applied whilst loading ChainLink, this is")
                .add("  a permanent error and you must either:").add()
                .add("   * Use the correct build of Forge for this version of ChainLink (%s)",
                        forgeMessage)
                .add()
                .add("   * Use a version of ChainLink for built for your version of Forge")
                .add().addWrapped(
                        "  The patch which failed requires Forge a build of %s but you are running build %d",
                        constraint.getRangeHumanReadable(),
                        ForgeVersion.getBuildVersion());
    }

    private PrettyPrinter patchConstraintFailed(PrettyPrinter errorPrinter,
            Constraint constraint, ConstraintViolationException ex) {
        return errorPrinter.add()
                .add("Oh dear. ChainLink could not apply one or more patches. A constraint check failed!")
                .add().hr('-').add()
                .add("A patch constraint violation was encountered whilst patching:")
                .add()
                .add("%20s : %s", "Constraint Name", constraint.getToken())
                .add("%20s : %s", "Your value", ex.getBadValue())
                .add("%20s : %s", "Allowed range",
                        constraint.getRangeHumanReadable());
    }

    private PrettyPrinter itsAllGoneHorriblyWrong(PrettyPrinter errorPrinter) {
        String forgeVer = "@FORGE_VERSION@";
        String forgeMessage = forgeVer == null
                ? "is usually specified in the ChainLink jar's filename"
                : "version is for " + forgeVer;

        return errorPrinter.add()
                .add("Oh dear. Something went wrong and the server had to shut down!")
                .add().hr('-').add()
                .add("A critical error was encountered while blending ChainLink with Forge!")
                .add().add("  Possible causes are:").add()
                .add("   * An incompatible Forge \"core mod\" is present. Try removing other mods to")
                .add("     see if the problem goes away.").add()
                .add("   * You are using the wrong version of Minecraft Forge. You must use the")
                .addWrapped(
                        "     correct version of Forge when using ChainLink, this %s (you are running %s)",
                        forgeMessage, ForgeVersion.getVersion())
                .add()
                .add("   * An error exists in ChainLink itself. Ensure you are running the latest version")
                .add("     of ChainLink.").add()
                .add("   * Gremlins are invading your computer. Did you feed a Mogwai after midnight?");
    }

    private PrettyPrinter appendTechnicalInfo(PrettyPrinter errorPrinter,
            String targetClassName, Throwable th, IMixinInfo mixin) {
        return errorPrinter.add().hr('-').add().add("Technical details:").add()
                .add("%20s : %s", "Failed on class", targetClassName)
                .add("%20s : %s", "During phase", mixin.getPhase())
                .add("%20s : %s", "Mixin", mixin.getName())
                .add("%20s : %s", "Config", mixin.getConfig().getName())
                .add("%20s : %s", "Error Type", th.getClass().getName())
                .add("%20s : %s", "Caused by",
                        th.getCause() == null ? "Unknown"
                                : th.getCause().getClass().getName())
                .add("%20s : %s", "Message", th.getMessage()).add();
    }

}