package io.github.yuazer.zaxworld.hook

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object TimeHook : PlaceholderExpansion {
    override val identifier: String = "zaxworld"
    override fun onPlaceholderRequest(player: Player?, args: String): String {
        if (args.equals("isFree", true)) {
            val isFree = if (player == null) false else ZaxWorldAPI.isFree(player)
            return isFree.toString()
        } else {
            val time = if (player == null) 0 else ZaxWorld.getPlayerCacheMap().getPlayerWorldTime(player.name, args)
            return "$time"
        }
    }
}