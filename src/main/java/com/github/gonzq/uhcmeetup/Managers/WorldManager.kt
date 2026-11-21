package com.github.gonzq.uhcmeetup.Managers

import com.github.gonzq.uhcmeetup.UhcMeetup
import org.bukkit.*
import java.io.File
import java.util.Arrays
import java.util.Random

class WorldManager {
    private val pl = UhcMeetup.pl
    private var borderCenterX: Double = 0.0
    private var borderCenterZ: Double = 0.0

    companion object{
       private val instance = WorldManager()
        fun getInstance(): WorldManager {
            return instance
        }
    }

    fun getMeetupWorld(): World? {
        return Bukkit.getWorld(pl.config.getConfig().getString("meetup-world")!!)
    }
    fun getLobby(): World? {
        return Bukkit.getWorld(pl.config.getConfig().getString("lobby.name")!!)
    }

    fun getLobbyLocation(): Location {
        val c = pl.config.getConfig();
        return Location(getLobby(), c.getInt("lobby.location.x",0).toDouble(), c.getInt("lobby.location.y", 50).toDouble(),
        c.getInt("lobby.location.z",0).toDouble(), c.getInt("lobby.location.yaw",0).toFloat(),
        c.getInt("lobby.location.pitch").toFloat());
    }

    fun setLobbyLocation(l: Location) {
        pl.config.getConfig().set("lobby.location.x", l.blockX);
        pl.config.getConfig().set("lobby.location.y", l.blockY);
        pl.config.getConfig().set("lobby.location.z", l.blockZ);
        pl.config.getConfig().set("lobby.location.yaw", l.yaw);
        pl.config.getConfig().set("lobby.location.pitch", l.pitch);
        pl.config.saveConfig()
    }

    fun getBorder(): Int {
        return (getMeetupWorld()!!.worldBorder.size / 2).toInt()
    }

    fun getBorderCenterX(): Double {
        return borderCenterX
    }

    fun getBorderCenterZ(): Double {
        return borderCenterZ
    }

    /**
     * Obtains both worlds.
     *
     * Worlds stored by Advanced Slime Paper are deserialised from their slime
     * file and loaded directly, which avoids the terrain generation freeze on
     * the main thread. When ASP is not installed, or the world was never
     * imported into the slime data source, we generate it the classic way.
     */
    fun createWorlds() {
        val lobbyName = pl.config.getConfig().getString("lobby.name")!!
        if (getLobby() == null) {
            val lobby = SlimeWorldSupport.load(lobbyName) ?: createVanillaLobby(lobbyName)
            applyLobbySettings(lobby)
        }

        val meetupName = pl.config.getConfig().getString("meetup-world")!!
        val w = Bukkit.getWorld(meetupName)
            ?: SlimeWorldSupport.load(meetupName)
            ?: createVanillaMeetup(meetupName)
        applyMeetupSettings(w)

        val random = Random()
        borderCenterX = (random.nextInt(201) - 100).toDouble()
        borderCenterX = (random.nextInt(201) - 100).toDouble()

        w.worldBorder.size = pl.config.getConfig().getInt("worldborder-size").toDouble()
        w.worldBorder.center = Location(w, borderCenterX, 0.0, borderCenterZ)
        w.worldBorder.damageBuffer = 0.0
    }

    private fun createVanillaLobby(name: String): World {
        val creator = WorldCreator(name)
        creator.type(WorldType.FLAT)
        creator.generateStructures(false)
        return creator.createWorld()!!
    }

    private fun applyLobbySettings(w: World) {
        w.difficulty = Difficulty.PEACEFUL
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false)
        w.setGameRule(GameRule.DO_WEATHER_CYCLE,false)
        w.setGameRule(GameRule.ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER,false)
        w.setGameRule(GameRule.DO_MOB_SPAWNING,false)
        w.setGameRule(GameRule.DO_INSOMNIA,false)
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false)
        w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        w.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
        w.setGameRule(GameRule.LOCATOR_BAR, false)
        w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
    }

    private fun createVanillaMeetup(name: String): World {
        val creator = WorldCreator(name)
        creator.type(WorldType.NORMAL)
        creator.generateStructures(false)
        creator.generatorSettings()
        val seeds = pl.config.getConfig().getLongList("seed-list")
        if (seeds.isNotEmpty()) {
            creator.seed(seeds[Random().nextInt(seeds.size)])
        }
        return creator.createWorld()!!
    }

    private fun applyMeetupSettings(w: World) {
        w.time = 9000
        w.difficulty = Difficulty.HARD
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false)
        w.setGameRule(GameRule.DO_WEATHER_CYCLE,false)
        w.setGameRule(GameRule.ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER,false)
        w.setGameRule(GameRule.DO_MOB_SPAWNING,false)
        w.setGameRule(GameRule.DO_INSOMNIA,false)
        w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false)
        w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        w.setGameRule(GameRule.NATURAL_REGENERATION,false)
        w.setGameRule(GameRule.REDUCED_DEBUG_INFO, false)
        w.setGameRule(GameRule.LOCATOR_BAR, false)
        w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
    }

    private fun deleteFile(path: File): Boolean{
        if (path.exists()) {
            val files = path.listFiles()
            if (files != null) {
                Arrays.stream(files).forEach(this::deleteFile)
            }
        }
        return path.delete()
    }

    fun deleteWorld() {
        val name = pl.config.getConfig().getString("meetup-world")!!
        // Slime worlds are persistent by design: keeping them is what makes the
        // next startup instant. Deleting must be opted into.
        if (pl.config.getConfig().getBoolean("slime-worlds.delete-on-restart", false)) {
            SlimeWorldSupport.delete(name)
        }
        if (getMeetupWorld() != null) run {
            val path = File(Bukkit.getWorldContainer(), name)
            deleteFile(path)
        }
    }
}
