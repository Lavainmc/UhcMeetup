package com.github.gonzq.uhcmeetup.Managers

import com.github.gonzq.uhcmeetup.UhcMeetup
import org.bukkit.World

/**
 * Optional bridge to Advanced Slime Paper (ASWM).
 *
 * ASP is a Paper *fork*, not a plugin: the API only exists when the server
 * itself is an AdvancedSlimePaper build. Every entry point here is therefore
 * guarded, and on a normal Spigot/Paper/Leaf server the plugin keeps using
 * vanilla world generation exactly as before.
 *
 * The point of the bridge: a world stored in the slime format is loaded by
 * deserialising a single file instead of generating terrain on the main
 * thread, which is what makes startup freeze for a few seconds today.
 */
object SlimeWorldSupport {

    @Volatile
    private var bridge: AspWorldBridge? = null

    /** True when the running server provides the ASP API and the feature is enabled. */
    val available: Boolean by lazy { setUp() }

    /** Human readable status, used in the startup log. */
    val status: String
        get() = when {
            !UhcMeetup.pl.config.getConfig().getBoolean("slime-worlds.enable", true) -> "disabled in config"
            available -> "Advanced Slime Paper detected"
            else -> "Advanced Slime Paper not installed, vanilla generation"
        }

    private fun setUp(): Boolean {
        if (!UhcMeetup.pl.config.getConfig().getBoolean("slime-worlds.enable", true)) return false
        return try {
            Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI")
            Class.forName("com.infernalsuite.asp.loaders.file.FileLoader")
            bridge = AspWorldBridge().apply { create() }
            true
        } catch (t: Throwable) {
            UhcMeetup.pl.logger.info("[slime] ASP API unavailable (${t.javaClass.simpleName}), falling back to vanilla worlds.")
            false
        }
    }

    fun exists(name: String): Boolean = safe(false) { it.exists(name) }

    /**
     * Loads [name] from the slime data source.
     * Returns null when the world is not stored there (or anything goes wrong),
     * so the caller can fall back to a generated world.
     */
    fun load(name: String, readOnly: Boolean = false): World? = safe(null) { it.load(name, readOnly) }

    fun save(name: String) {
        safe(Unit) { it.save(name) }
    }

    fun delete(name: String) {
        safe(Unit) { it.delete(name) }
    }

    private inline fun <T> safe(fallback: T, block: (AspWorldBridge) -> T): T {
        if (!available) return fallback
        val b = bridge ?: return fallback
        return try {
            block(b)
        } catch (t: Throwable) {
            UhcMeetup.pl.logger.warning("[slime] operation failed: ${t.javaClass.simpleName}: ${t.message}")
            fallback
        }
    }
}

/**
 * Kept apart from [SlimeWorldSupport] on purpose: this class references ASP
 * types directly, so it is only ever loaded after the class presence check.
 */
internal class AspWorldBridge {

    private lateinit var api: com.infernalsuite.asp.api.AdvancedSlimePaperAPI
    private lateinit var loader: com.infernalsuite.asp.api.loaders.SlimeLoader

    fun create() {
        val config = UhcMeetup.pl.config.getConfig()
        val folder = java.io.File(
            org.bukkit.Bukkit.getWorldContainer(),
            config.getString("slime-worlds.folder", "slime_worlds") ?: "slime_worlds"
        )
        if (!folder.exists()) folder.mkdirs()
        loader = com.infernalsuite.asp.loaders.file.FileLoader(folder)
        api = com.infernalsuite.asp.api.AdvancedSlimePaperAPI.instance()
        UhcMeetup.pl.logger.info("[slime] using data source folder ${folder.path}")
    }

    fun exists(name: String): Boolean = loader.worldExists(name)

    fun load(name: String, readOnly: Boolean): World? {
        if (!loader.worldExists(name)) return null
        val properties = com.infernalsuite.asp.api.world.properties.SlimePropertyMap()
        properties.setValue(
            com.infernalsuite.asp.api.world.properties.SlimeProperties.ENVIRONMENT,
            if (name.endsWith("_nether")) "nether" else "normal"
        )
        val slimeWorld = api.readWorld(loader, name, readOnly, properties)
        return api.loadWorld(slimeWorld, true).bukkitWorld
    }

    fun save(name: String) {
        val loaded = api.getLoadedWorld(name) ?: return
        api.saveWorld(loaded.serializableCopy)
    }

    fun delete(name: String) {
        if (!loader.worldExists(name)) return
        val loaded = api.getLoadedWorld(name)
        if (loaded != null) org.bukkit.Bukkit.unloadWorld(loaded.bukkitWorld, false)
        loader.deleteWorld(name)
    }
}
