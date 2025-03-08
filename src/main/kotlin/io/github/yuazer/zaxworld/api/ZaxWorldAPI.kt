package io.github.yuazer.zaxworld.api

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.runnable.WorldRunnable
import io.github.yuazer.zaxworld.utils.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ZaxWorldAPI {
    fun isWorldInCheck(world:String):Boolean{
        return ZaxWorld.config.getConfigurationSection("World")?.getKeys(deep = false)?.contains(world) ?: false
    }
    //手动开启一次玩家世界检测
    fun checkPlayer(player: Player): Unit {
        val playerName = player.name
        val worldName = player.world.name
        val worldList = ZaxWorld.config.getConfigurationSection("World")?.getKeys(false) ?: emptySet()
        if (!worldList.contains(worldName)){
            ZaxWorld.getPlayerCacheMap().getPlayerAllWorld(playerName).forEach {
                ZaxWorld.runnableManager.stop(playerName, it)
            }
            return
        }else{
            ZaxWorld.getPlayerCacheMap().getPlayerAllWorld(playerName).forEach {
                ZaxWorld.runnableManager.stop(playerName, it)
            }
            val worldRunnable = WorldRunnable(playerName,worldName)
            ZaxWorld.runnableManager.add(playerName , worldName,worldRunnable)
            val ketherList = PlayerUtils.replaceInList(ZaxWorld.config.getStringList("World.${worldName}.joinScript"),"%player_name%",playerName)
            PlayerUtils.runKether(ketherList, Bukkit.getPlayer(playerName)?:return)
            ZaxWorld.runnableManager.asyncStart(playerName,worldName)
        }
    }
    fun checkPlayer(player: Player, worldName: String) {
        val playerName = player.name
        val worldList = ZaxWorld.config.getConfigurationSection("World")?.getKeys(false) ?: emptySet()
        if (!worldList.contains(worldName)){
            ZaxWorld.getPlayerCacheMap().getPlayerAllWorld(playerName).forEach {
                ZaxWorld.runnableManager.stop(playerName, it)
            }
            return
        }else{
            ZaxWorld.getPlayerCacheMap().getPlayerAllWorld(playerName).forEach {
                ZaxWorld.runnableManager.stop(playerName, it)
            }
            val worldRunnable = WorldRunnable(playerName,worldName)
            ZaxWorld.runnableManager.add(playerName , worldName,worldRunnable)
            val ketherList = PlayerUtils.replaceInList(ZaxWorld.config.getStringList("World.${worldName}.joinScript"),"%player_name%",playerName)
            PlayerUtils.runKether(ketherList, Bukkit.getPlayer(playerName)?:return)
            ZaxWorld.runnableManager.asyncStart(playerName,worldName)
        }
    }
    fun isFree(player: Player):Boolean{
        return ZaxWorld.getPlayerFreeSet().contains(player.name)
    }
    fun isFree(playerName: String):Boolean{
        return ZaxWorld.getPlayerFreeSet().contains(playerName)
    }

}