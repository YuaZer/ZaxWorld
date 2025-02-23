package io.github.yuazer.zaxworld.runnable

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import io.github.yuazer.zaxworld.utils.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class WorldRunnable(var playerName:String,var worldName:String): BukkitRunnable() {
    private val worldList = ZaxWorld.config.getConfigurationSection("World")?.getKeys(false) ?: emptySet()
    private val timeSpecialList = ZaxWorld.config.getConfigurationSection("World.${worldName}.inTime")?.getKeys(false)
        ?: emptySet()
    override fun run() {
        if (worldList.contains(worldName)){
            var time = ZaxWorld.getPlayerCacheMap().getPlayerWorldTime(playerName, worldName)
            val player = Bukkit.getPlayer(playerName)
            if (timeSpecialList.contains(time.toString())){
                val ketherList = PlayerUtils.replaceInList(ZaxWorld.config.getStringList("World.${worldName}.inTime.${time}"),"%player_name%",playerName)
                PlayerUtils.runKether(ketherList, player ?:return)
            }
            if (time<=0){
                val ketherList = PlayerUtils.replaceInList(ZaxWorld.config.getStringList("World.${worldName}.endScript"),"%player_name%",playerName)
                PlayerUtils.runKether(ketherList, player ?:return)
                ZaxWorld.runnableManager.stop(playerName, worldName)
                return
            }
            if (ZaxWorldAPI.isFree(playerName)){
                return
            }
            player?.let {
                if (player.hasPermission("zaxworld.free.${worldName}")){
                    return
                }
            }
            time--
            ZaxWorld.getPlayerCacheMap().set(playerName,worldName,time)
        }else{
            ZaxWorld.runnableManager.stop(playerName, worldName)
        }
    }
}