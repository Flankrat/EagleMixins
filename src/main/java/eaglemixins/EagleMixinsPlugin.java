package eaglemixins;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class EagleMixinsPlugin implements IFMLLoadingPlugin {

	public EagleMixinsPlugin() {
		MixinBootstrap.init();

		// Temporary FUR 1.4.2 fixes because we are not using 1.5.0+
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.furold.json", () -> Loader.isModLoaded("mod_lavacow") && Loader.instance().getIndexedModList().get("mod_lavacow").getVersion().equals("1.4.2"));

		// Biome tag based worldgen blacklists (these were never enqueued, so the config options had no effect)
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.iceandfire.pixievillageblacklist.json", () -> Loader.isModLoaded("iceandfire"));
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.lycanitesmobs.disablegeneration.json", () -> Loader.isModLoaded("lycanitesmobs"));

		// Ice and Fire's dread stone stairs/slabs pick up whichever mod is active as their namespace (nuclearcraft in Dregora), always register them as iceandfire
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.iceandfire.dreadregistrynames.json", () -> Loader.isModLoaded("iceandfire"));

		// Decouples the OTG overworld preset from level-name, controlled by the "OTG Overworld Preset" server option
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.otg.overworldpreset.json", () -> Loader.isModLoaded("openterraingenerator"));

		//Debug stuff
		FermiumRegistryAPI.enqueueMixin(false, "mixins.eaglemixins.debug.vanilla.json", EagleMixins.debugEnabled);
		FermiumRegistryAPI.enqueueMixin(true, "mixins.eaglemixins.debug.otg.json", () -> EagleMixins.debugEnabled && Loader.isModLoaded("openterraingenerator"));
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
			MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
			CoreModManager.getReparseableCoremods().removeIf(s -> StringUtils.containsIgnoreCase(s, "fermiumbooter"));
		}
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}