package io.github.yuazer.zaxworld.database

import io.github.yuazer.zaxworld.ZaxWorld
import taboolib.common.platform.function.submitAsync

object DataLoader{
    private val dataSQL = DataSQL()
    fun loadData2Cache_Async(){
        submitAsync(now = true, delay = 0) {
            dataSQL.listAllUsers().forEach {username->
                val userAllData:List<Map<String, Int>> = dataSQL.queryUserWorldsAndTimes(username)
                for (data:Map<String,Int> in userAllData){
                    data.keys.forEach(action = {world->
                        val totalName:String = ZaxWorld.getPlayerCacheMap().getPWKey(username,world)
                        val defaultTime = ZaxWorld.config.getInt("World.${world}.time")
                        ZaxWorld.getPlayerCacheMap().getMap()[totalName] = data[world] ?:defaultTime
                    })
                }
            }
        }
    }
    fun saveDataFromCache_Async(){
        submitAsync(now = true) {
            val dataMap: MutableMap<String, Int> = ZaxWorld.getPlayerCacheMap().getMap()
            dataMap.keys.forEach {
                val playerName = it.split("<>")[0]
                val worldName = it.split("<>")[1]
                val defaultTime = ZaxWorld.config.getInt("World.${worldName}.time")
                dataSQL.upsertPlayerData(playerName,worldName,dataMap[it]?:defaultTime)
            }
        }
    }
}
