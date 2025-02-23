package io.github.yuazer.zaxworld.listener

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import taboolib.common.platform.event.SubscribeEvent

object PlayerEvent {
    @SubscribeEvent
    fun onJoin(e: PlayerJoinEvent){
        val player = e.player
        ZaxWorldAPI.checkPlayer(player)
    }
    @SubscribeEvent
    fun onQuit(e:PlayerQuitEvent){
        val player=e.player
        val worldName = player.world.name
        ZaxWorld.runnableManager.stop(player.name, worldName)
    }
    @SubscribeEvent
    fun onRespawn(e:PlayerRespawnEvent){
        val player = e.player
        ZaxWorldAPI.checkPlayer(player)
    }
}