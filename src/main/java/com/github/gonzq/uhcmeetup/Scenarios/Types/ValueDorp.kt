package com.github.gonzq.uhcmeetup.Scenarios.Types

import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Managers.ScenarioManager
import com.github.gonzq.uhcmeetup.Managers.WorldManager
import com.github.gonzq.uhcmeetup.Scenarios.Scenario
import com.github.gonzq.uhcmeetup.Utils.Utils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class ValueDorp: Listener, Scenario("附加掉落", ItemStack(Material.NETHERITE_SCRAP),
"&7玩家死亡时:", "&7100% 概率掉落: 下界合金碎片x2 & 金锭x4","&730% 概率掉落: 沉重核心x1") {
    private var b = false
    private val scen = UhcMeetup.pl.scenarios

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val p = e.entity
        if (p.world == UhcMeetup.pl.world.getMeetupWorld()) {
            e.drops.add(ItemStack(Material.NETHERITE_SCRAP,2))
            e.drops.add(ItemStack(Material.GOLD_INGOT,4))
            if (Utils.getRandomInt(100) - (30) <= 1) {
                e.drops.add(ItemStack(Material.HEAVY_CORE,1))
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