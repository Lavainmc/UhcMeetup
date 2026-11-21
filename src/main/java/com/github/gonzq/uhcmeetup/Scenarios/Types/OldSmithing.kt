package com.github.gonzq.uhcmeetup.Scenarios.Types

import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Managers.ScenarioManager
import com.github.gonzq.uhcmeetup.Scenarios.Scenario
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

class OldSmithing: Listener, Scenario("无模锻造", ItemStack(Material.SMITHING_TABLE),
    "&7使用下界合金锭锻造升级钻石装备时无需模板") {
    private var b = false
    private val scen = UhcMeetup.pl.scenarios

    override fun setEnabled(enable: Boolean) {
        b = enable
    }

    override fun isEnabled(): Boolean {
        return b
    }
}