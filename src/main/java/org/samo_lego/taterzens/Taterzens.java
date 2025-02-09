package org.samo_lego.taterzens;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.samo_lego.taterzens.commands.NpcCommand;
import org.samo_lego.taterzens.commands.TaterzensCommand;
import org.samo_lego.taterzens.event.BlockInteractEvent;
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.samo_lego.taterzens.storage.TaterConfig;
import org.samo_lego.taterzens.storage.TaterLang;

import java.io.File;
import java.util.LinkedHashSet;

public class Taterzens implements ModInitializer {

    public static final String MODID = "taterzens";

    /**
     * Configuration file.
     */
    public static TaterConfig config;
    /**
     * Language file.
     */
    public static TaterLang lang;
    private static final Logger LOGGER = (Logger) LogManager.getLogger();
    /**
     * List of **loaded** {@link TaterzenNPC TaterzenNPCs}.
     */
    public static final LinkedHashSet<TaterzenNPC> TATERZEN_NPCS = new LinkedHashSet<>();
    private static File taterDir;

    /**
     * Taterzen entity type. Used server - only, as it is replaced with vanilla type
     * when packets are sent.
     */
    public static final EntityType<TaterzenNPC> TATERZEN = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MODID, "npc"),
            FabricEntityTypeBuilder
                    .<TaterzenNPC>create(SpawnGroup.MONSTER, TaterzenNPC::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                    .disableSummon()
                    .build()
    );

    @Override
    public void onInitialize() {
        // Events
        CommandRegistrationCallback.EVENT.register(TaterzensCommand::register);
        CommandRegistrationCallback.EVENT.register(NpcCommand::register);
        UseBlockCallback.EVENT.register(new BlockInteractEvent());

        FabricDefaultAttributeRegistry.register(TATERZEN, TaterzenNPC.createMobAttributes());

        taterDir = new File(FabricLoader.getInstance().getConfigDir() + "/Taterzens/presets");
        if (!taterDir.exists() && !taterDir.mkdirs())
            throw new RuntimeException(String.format("[%s] Error creating directory!", MODID));
        taterDir = taterDir.getParentFile();

        config = TaterConfig.loadConfigFile(new File(taterDir + "/config.json"));
        lang = TaterLang.loadLanguageFile(new File(taterDir + "/" + config.language + ".json"));
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Gets the minecraft Taterzens config directory.
     * @return config directory folder.
     */
    public static File getTaterDir() {
        return taterDir;
    }
}
