package com.github.gonzq.uhcmeetup.Managers

import com.github.gonzq.uhcmeetup.Enums.PlayerState
import com.github.gonzq.uhcmeetup.Players.GamePlayer
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Biome
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class ScatterManager {

    companion object {
        private var instance = ScatterManager()

        fun getInstance(): ScatterManager {
            return instance
        }

        private val REPLACEABLE_MATERIALS: Set<Material> = setOf(
            Material.AIR,
            Material.WATER,
            Material.LAVA,
            Material.VINE,
            Material.TALL_GRASS,
            Material.SHORT_GRASS,
            Material.PINK_PETALS,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY,
            Material.WITHER_ROSE,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
            Material.CHORUS_FLOWER,
            Material.CHORUS_PLANT,
            Material.TORCHFLOWER,
            Material.PITCHER_PLANT,
            Material.FERN,
            Material.LARGE_FERN,
            Material.DEAD_BUSH,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.KELP,
            Material.KELP_PLANT,
            Material.SUGAR_CANE,
            Material.BAMBOO,
            Material.BAMBOO_SAPLING,
            Material.CACTUS,
            Material.NETHER_SPROUTS,
            Material.WARPED_ROOTS,
            Material.CRIMSON_ROOTS,
            Material.WARPED_FUNGUS,
            Material.CRIMSON_FUNGUS,
            Material.SPORE_BLOSSOM,
            Material.BIG_DRIPLEAF,
            Material.SMALL_DRIPLEAF,
            Material.HANGING_ROOTS,
            Material.GLOW_LICHEN,
            Material.MOSS_CARPET,
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.AZALEA_LEAVES,
            Material.FLOWERING_AZALEA_LEAVES,
            Material.MANGROVE_LEAVES,
            Material.CHERRY_LEAVES,
            Material.NETHER_WART_BLOCK,
            Material.WARPED_WART_BLOCK,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM,
            Material.SHROOMLIGHT,
            Material.WEEPING_VINES,
            Material.TWISTING_VINES,
            Material.LILY_PAD
        )

        // 预创建的状态效果对象
        private val SCATTER_EFFECTS = listOf(
            PotionEffect(PotionEffectType.RESISTANCE, 12000, 5),
            PotionEffect(PotionEffectType.BLINDNESS, 12000, 5)
        )

        // 水域生物群系列表
        private val WATER_BIOMES = setOf(
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
            Biome.RIVER,
            Biome.FROZEN_RIVER
        )

        private val INVALID_MATERIALS = setOf(
            Material.LAVA,
            Material.WATER,
            Material.VOID_AIR
        )
    }

    fun scatter(p: GamePlayer) {
        val player = p.getPlayer()!!
        val location = calculateCoords()

        createGlassCage(location)

        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)

        SCATTER_EFFECTS.forEach { effect ->
            player.addPotionEffect(effect)
        }

        player.gameMode = GameMode.ADVENTURE
        player.isInvulnerable = true
        p.setKit()

        // title消息
        val subtitle = String.format(
            "§3加载地形中...",
        )
        val title = "§b⚡ 游戏即将开始 ⚡"
        //send title
        player.sendTitle(title, subtitle, 20, 400, 20)
    }

    private fun createGlassCage(location: Location) {
        val world = location.world ?: return

        val x = location.blockX
        val y = location.blockY
        val z = location.blockZ

        for (dx in -1..1) {
            for (dy in -1..2) {
                for (dz in -1..1) {
                    if (dx == 0 && dz == 0 && (dy == 0 || dy == 1)) continue

                    val blockLoc = Location(world, (x + dx).toDouble(), (y + dy).toDouble(), (z + dz).toDouble())
                    val block = blockLoc.block

                    if (REPLACEABLE_MATERIALS.contains(block.type)) {
                        block.type = Material.GLASS
                    }
                }
            }
        }
    }

    fun removeGlassCage(location: Location) {
        val world = location.world ?: return

        val x = location.blockX
        val y = location.blockY
        val z = location.blockZ

        for (dx in -1..1) {
            for (dy in -1..2) {
                for (dz in -1..1) {
                    val blockLoc = Location(world, (x + dx).toDouble(), (y + dy).toDouble(), (z + dz).toDouble())
                    val block = blockLoc.block

                    if (block.type == Material.GLASS) {
                        block.type = Material.AIR
                    }
                }
            }
        }
    }

    fun postScatter(p: GamePlayer) {
        val player = p.getPlayer() ?: return
        removeGlassCage(player.location)
        p.getPlayer()!!.activePotionEffects.forEach { pe -> p.getPlayer()!!.removePotionEffect(pe.type) }
        player.gameMode = GameMode.SURVIVAL
        player.isInvulnerable = false
        p.setState(PlayerState.PLAYING)
        StatsManager.getInstance().add(p, StatsManager.Stats.PLAYED)

        val subtitle = String.format(
            "§3战斗!",
        )
        val title = "§b⚔ 游戏开始 ⚔"
        //send title
        player.sendTitle(title, subtitle, 20, 40, 20)
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1.0f, 1.0f)
    }

    private fun calculateCoords(): Location {
        val wm = WorldManager.getInstance()
        val rand = Random()
        val upperbound = wm.getBorder()
        var x = rand.nextInt(upperbound).toDouble() + 0.5
        var z = rand.nextInt(upperbound).toDouble() + 0.5
        val randDouble = Math.random()
        val randDouble2 = Math.random()
        if (randDouble <= 0.5) x *= -1
        if (randDouble2 <= 0.5) z *= -1
        val y = wm.getMeetupWorld()!!.getHighestBlockYAt(x.toInt(),z.toInt()) + 1
        val l = Location(wm.getMeetupWorld(), x,y.toDouble(),z);
        return if (isInvalidLocation(l)) calculateCoords() else l;
    }


    private fun isInvalidLocation(location: Location): Boolean {
        val block = location.block

        if (WATER_BIOMES.contains(block.biome)) {
            return true
        }

        if (INVALID_MATERIALS.contains(block.type)) {
            return true
        }

        val belowBlock = location.world?.getBlockAt(location.blockX, location.blockY - 1, location.blockZ)
        if (belowBlock != null && INVALID_MATERIALS.contains(belowBlock.type)) {
            return true
        }

        return false
    }
}