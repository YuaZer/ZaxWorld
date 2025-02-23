package io.github.yuazer.zaxworld.listener

import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.event.player.PlayerChangedWorldEvent
import taboolib.common.platform.event.SubscribeEvent

object WorldEvent {

    @SubscribeEvent
    fun onChangeWorld(e:PlayerChangedWorldEvent) {
        val player = e.player
        ZaxWorldAPI.checkPlayer(player)
    }
}