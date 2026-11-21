package com.github.gonzq.uhcmeetup

import com.github.gonzq.uhcmeetup.File.CFiles
import com.github.gonzq.uhcmeetup.Managers.GameManager
import com.github.gonzq.uhcmeetup.Managers.PlayerManager
import com.github.gonzq.uhcmeetup.Managers.ScatterManager
import com.github.gonzq.uhcmeetup.Managers.ScenarioManager
import com.github.gonzq.uhcmeetup.Managers.SlimeWorldSupport
import com.github.gonzq.uhcmeetup.Managers.StatsManager
import com.github.gonzq.uhcmeetup.Managers.WorldManager
import com.github.gonzq.uhcmeetup.Tasks.ScoreboardTask
import com.github.gonzq.uhcmeetup.Utils.MySqlUtils
import com.github.gonzq.uhcmeetup.Utils.Placeholders.Placeholders
import com.github.gonzq.uhcmeetup.Utils.Placeholders.PlaceholdersStats
import com.github.gonzq.uhcmeetup.Utils.Utils
import fr.mrmicky.fastboard.FastBoard
import fr.mrmicky.fastinv.FastInvManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Objective

class UhcMeetup : JavaPlugin() {
    companion object {
        lateinit var pl: UhcMeetup
    }
    lateinit var config: CFiles
    lateinit var board: CFiles
    lateinit var kit: CFiles
    lateinit var lang: CFiles
    lateinit var databases: CFiles

    // Per-match state. Owned here (not in static singletons) so that
    // several matches can coexist once rooms are introduced.
    lateinit var pm: PlayerManager
    lateinit var game: GameManager
    lateinit var world: WorldManager
    lateinit var scenarios: ScenarioManager
    lateinit var scatter: ScatterManager

    lateinit var prefix: String
    lateinit var borderPrefix: String

    lateinit var mysql: MySqlUtils
    var isMysql = false

    var boards = mutableMapOf<String, FastBoard>()

    var papi = false



    override fun onEnable() {
        // Plugin startup logic
        pl = this
        config = CFiles(this, "config.yml")
        board = CFiles(this, "scoreboard.yml")
        kit = CFiles(this, "kit.yml")
        lang = CFiles(this, "lang.yml")
        databases = CFiles(this, "databases.yml")

        isMysql = databases.getConfig().getBoolean("databases.mysql.enable")
        prefix = Utils.chat(lang.getConfig().getString("prefix"))
        borderPrefix = Utils.chat(lang.getConfig().getString("border-prefix"))
        // StatsManager stays a singleton on purpose: player statistics are
        // shared across matches, they are not part of a single game.
        StatsManager.getInstance().setUpStats()
        pm = PlayerManager(this)
        world = WorldManager(this)
        scenarios = ScenarioManager(this)
        scatter = ScatterManager(this)
        game = GameManager(this)
        FastInvManager.register(this)
        logger.info("[worlds] " + SlimeWorldSupport.status)
        world.deleteWorld()
        world.createWorlds()

        ScoreboardTask().runTaskTimer(this,0,20)

        if (server.pluginManager.getPlugin("PlaceholderAPI") == null) {
            papi = false
        } else {
            papi = true
            Placeholders().register()
       //     PlaceholdersStats().register()
        }
        Utils.setObjectives()
        Utils.registerAll()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        if (config.getConfig().getBoolean("bungee-support.send-to-hub.enable")) {
            Bukkit.getOnlinePlayers().forEach { p ->
                Utils.sendToBungeeHub(
                    p, config.getConfig()
                        .getString("bungee-support.send-to-hub.server-name")!!
                )
            }
        }
        Bukkit.getScoreboardManager()!!.mainScoreboard.objectives.forEach(Objective::unregister)
        world.deleteWorld()
        StatsManager.getInstance().saveStats()
        if (isMysql) {
            mysql.disconnect()
        }
    }
}
