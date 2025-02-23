package io.github.yuazer.zaxworld.mymap

import io.github.yuazer.zaxworld.ZaxWorld

object PlayerWorldMap{
    private var mutableMap:MutableMap<String,Int> = mutableMapOf()
    fun getMap():MutableMap<String,Int>{
        return this.mutableMap
    }
    fun getPlayerWorldTime(playerName:String,worldName:String): Int {
        val defaultTime = ZaxWorld.config.getInt("World.${worldName}.time")
        return mutableMap[getPWKey(playerName,worldName)] ?:defaultTime
    }
    fun getPWKey(playerName: String,worldName: String):String{
        return "$playerName<>$worldName"
    }
    fun getPlayerAllWorld(playerName: String):MutableSet<String>{
        val worldSet = mutableSetOf<String>()
        mutableMap.keys.forEach {
            val name = it.split("<>")[0]
            if (playerName.equals(name,ignoreCase = true)){
                worldSet.add(it.split("<>")[1])
            }
        }
        return worldSet
    }
    fun add(playerName: String,worldName: String,time:Int){
        val defaultTime = ZaxWorld.config.getInt("World.${worldName}.time")
        set(playerName, worldName, mutableMap[getPWKey(playerName, worldName)]?.plus(time) ?: (defaultTime + time))
    }
    fun reduce(playerName: String,worldName: String,time:Int){
        val defaultTime = ZaxWorld.config.getInt("World.${worldName}.time")
        set(playerName, worldName, mutableMap[getPWKey(playerName, worldName)]?.minus(time) ?: (defaultTime - time))
    }
    fun set(playerName: String,worldName: String,time:Int){
        mutableMap[getPWKey(playerName,worldName)] = time
    }
}
