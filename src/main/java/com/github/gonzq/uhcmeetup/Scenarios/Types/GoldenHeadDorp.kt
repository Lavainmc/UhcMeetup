package com.github.gonzq.uhcmeetup.Scenarios.Types

import com.github.gonzq.uhcmeetup.Managers.ScenarioManager
import com.github.gonzq.uhcmeetup.Managers.WorldManager
import com.github.gonzq.uhcmeetup.Scenarios.Scenario
import com.github.gonzq.uhcmeetup.Utils.Utils
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

class GoldenHeadDorp: Listener, Scenario("金头掉落", ItemStack(Material.GOLDEN_APPLE),
"&7玩家死亡时会掉落一个金头","&6金头","&9伤害吸收 I (02:00)","&9生命恢复 III (00:05)") {
    private var b = false
    private val scen = ScenarioManager.getInstance()

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        val p = e.entity
        if (p.world == WorldManager.getInstance().getMeetupWorld()) {
            e.drops.add(ItemStack(Utils.goldenHead()))
        }
    }

    override fun setEnabled(enable: Boolean) {
        b = enable
    }

    override fun isEnabled(): Boolean {
        return b
    }
}