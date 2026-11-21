package com.github.gonzq.uhcmeetup.Guis

import com.github.gonzq.uhcmeetup.Managers.StatsManager
import com.github.gonzq.uhcmeetup.UhcMeetup
import com.github.gonzq.uhcmeetup.Utils.Utils
import fr.mrmicky.fastinv.FastInv
import fr.mrmicky.fastinv.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player

class TopStatsGui(player: Player): FastInv(9, "最高游玩数据") {
    val pl = UhcMeetup.pl
    val stats = StatsManager.getInstance()


    init {
        val p = pl.pm.getUhcPlayer(player)
        addItem(ItemBuilder(Material.GOLD_BLOCK).name(Utils.chat("&a最高胜利")).lore(stats.getTop(StatsManager.Stats.WINS)).build())
        addItem(ItemBuilder(Material.SKELETON_SKULL).name(Utils.chat("&c最高死亡")).lore(stats.getTop(StatsManager.Stats.DEATHS)).build())
        addItem(ItemBuilder(Material.IRON_SWORD).name(Utils.chat("&c最高击杀")).lore(stats.getTop(StatsManager.Stats.KILLS)).build())
        addItem(ItemBuilder(Material.GOLDEN_APPLE).name(Utils.chat("&6最高金苹果食用数")).lore(stats.getTop(StatsManager.Stats.GAPPS)).build())
        addItem(ItemBuilder(Material.ARROW).name(Utils.chat("&b最高弓箭发射")).lore(stats.getTop(StatsManager.Stats.BOW_SHOOTS)).build())
        addItem(ItemBuilder(Material.GRASS_BLOCK).name(Utils.chat("&a最高游玩次数")).lore(stats.getTop(StatsManager.Stats.PLAYED)).build())
        addClickHandler{e -> e.isCancelled = true}
    }
}