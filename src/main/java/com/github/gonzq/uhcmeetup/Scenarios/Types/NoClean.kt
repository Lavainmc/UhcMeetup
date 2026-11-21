package com.github.gonzq.uhcmeetup.Scenarios.Types

import com.github.gonzq.uhcmeetup.Scenarios.Scenario
import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Utils.Utils
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.SpectralArrow
import org.bukkit.entity.Trident
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class NoClean(val pl: UhcMeetup, val time: Int): Listener, Scenario("战后保护", ItemStack(Material.QUARTZ),
"&7你在杀死玩家后 $time 秒内不会受伤", "&c无敌时间内攻击其他玩家会取消无敌。") {
    private var b = false
    private val noCleanList = mutableListOf<UUID>()

    @EventHandler
    fun noClean(e: PlayerDeathEvent) {
        if (e.entity.killer != null) {
            val k = e.entity.killer!!
            k.isInvulnerable = true
            noCleanList.add(k.uniqueId)
            k.sendMessage(Utils.chat(pl.prefix + pl.lang.getConfig().getString("noclean")!!
                .replace("%time%", "$time")))

            Utils.delay(time * 20L) {
                if (noCleanList.contains(k.uniqueId)) {
                    noCleanList.remove(k.uniqueId)
                    k.isInvulnerable = false
                    k.sendMessage(Utils.chat("${pl.prefix}无敌时间结束，你已不再处于无敌状态!"))
                }
            }
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        val p = e.entity
        val damager = e.damager
        if (p is Player) {
            if (damager is Player) {
                if (noCleanList.contains(damager.uniqueId)) {
                    noCleanList.remove(damager.uniqueId)
                    damager.isInvulnerable = false
                    damager.sendMessage(Utils.chat("${pl.prefix}§c你攻击了他人，你已退出无敌状态!"))
                }
            } else if (damager is Arrow && damager.shooter is Player) {
                val d = damager.shooter as Player
                if (noCleanList.contains(d.uniqueId)) {
                    noCleanList.remove(d.uniqueId)
                    d.isInvulnerable = false
                    d.sendMessage(Utils.chat("${pl.prefix}§c你攻击了他人，你已退出无敌状态!"))
                }
            } else if (damager is SpectralArrow && damager.shooter is Player) {
                val d = damager.shooter as Player
                if (noCleanList.contains(d.uniqueId)) {
                    noCleanList.remove(d.uniqueId)
                    d.isInvulnerable = false
                    d.sendMessage(Utils.chat("${pl.prefix}§c你攻击了他人，你已退出无敌状态!"))
                }
            } else if (damager is Trident && damager.shooter is Player) {
                val d = damager.shooter as Player
                if (noCleanList.contains(d.uniqueId)) {
                    noCleanList.remove(d.uniqueId)
                    d.isInvulnerable = false
                    d.sendMessage(Utils.chat("${pl.prefix}§c你攻击了他人，你已退出无敌状态!"))
                }
            }
        }
    }

    override fun setEnabled(enable: Boolean) {
        b = enable
    }

    override fun isEnabled(): Boolean {
        return b
    }
}