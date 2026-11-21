package com.github.gonzq.uhcmeetup.Tasks

import com.github.gonzq.uhcmeetup.Managers.GameManager
import com.github.gonzq.uhcmeetup.Managers.PlayerManager
import com.github.gonzq.uhcmeetup.Managers.WorldManager
import com.github.gonzq.uhcmeetup.Players.GamePlayer
import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Utils.Utils
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

class BorderTask: BukkitRunnable() {
    private var pl = UhcMeetup.pl
    private var time = 600
    private var wtime = pl.config.getConfig().getInt("worldborder-time")
    private var game = UhcMeetup.pl.game


    override fun run() {
        if (time == 600) Bukkit.broadcastMessage(Utils.chat(pl.borderPrefix + pl.lang.getConfig().getString("border-shrink-one-minute")))
        if (time == 540) {
            Bukkit.broadcastMessage(Utils.chat(pl.borderPrefix + pl.lang.getConfig().getString("border-shrink")))
            UhcMeetup.pl.world.getMeetupWorld()!!.worldBorder.setSize(20.0, wtime.toLong())
            BorderActionTask().runTaskTimer(pl,0,20)
            cancel()
        }
        if (time == 0) {
            Bukkit.broadcastMessage(Utils.chat(pl.borderPrefix + pl.lang.getConfig().getString("border-shrink")))
            UhcMeetup.pl.world.getMeetupWorld()!!.worldBorder.setSize(4.0, 30)
            BorderActionTask().runTaskTimer(pl,0,20)
            cancel()
        }
        game.setTimeElapsed(game.getTimeElapsed() + 1)
        time--
    }
}