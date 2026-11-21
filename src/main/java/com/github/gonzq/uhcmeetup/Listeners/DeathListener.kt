package com.github.gonzq.uhcmeetup.Listeners

import com.github.gonzq.uhcmeetup.Enums.GameState
import com.github.gonzq.uhcmeetup.Enums.PlayerState
import com.github.gonzq.uhcmeetup.Managers.GameManager
import com.github.gonzq.uhcmeetup.Managers.StatsManager
import com.github.gonzq.uhcmeetup.Managers.WorldManager
import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Utils.Utils
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

class DeathListener: Listener {
    private val pl = UhcMeetup.pl
    private val game = GameManager.getInstance()
    private val world = WorldManager.getInstance()

    init {
        pl.server.pluginManager.registerEvents(this, pl)
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val p = pl.pm.getUhcPlayer(e.entity)
        val killer = p.getPlayer()!!.killer

        if (game.getState() == GameState.STARTED) {
            if (killer != null) {
                val k = pl.pm.getUhcPlayer(killer)
                k.addKill()
                killer.playSound(killer.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 5.0f, 1.0f)
            }
            p.setState(PlayerState.SPECTATING)
            pl.pm.checkIfRemainingPlayers()
            StatsManager.getInstance().add(p, StatsManager.Stats.DEATHS)
            Utils.delay(1) {
                p.getPlayer()!!.teleport(Location(world.getMeetupWorld(),0.0,100.0,0.0))
                p.getPlayer()!!.gameMode = GameMode.SPECTATOR
            }
        }
    }
    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val player = e.player

        if (game.getState() == GameState.STARTED) {
            player.gameMode = GameMode.SPECTATOR
            val subtitle = String.format(
                "§4再接再厉...",
            )
            val title = "§c☠ 出局 ☠"
            player.sendTitle(title, subtitle, 0, 60, 20)
            Utils.delay(1) {
                player.sendMessage(Utils.chat("${pl.prefix}§c你已死亡! &f点按数字键可传送到其他玩家!"))
                player.teleport(Location(world.getMeetupWorld(), 0.0, 100.0, 0.0))
            }
        }
    }
}