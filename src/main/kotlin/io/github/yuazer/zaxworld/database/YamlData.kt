package io.github.yuazer.zaxworld.database

import io.github.yuazer.zaxworld.ZaxWorld
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submitAsync
import taboolib.common5.util.replace
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

class YamlData {
    companion object{
        fun saveYamlData(){
            submitAsync(now = true) {
                val dataMap: MutableMap<String, Int> = ZaxWorld.getPlayerCacheMap().getMap()
                dataMap.keys.forEach {
                    val playerName = it.split("<>")[0]
                    val worldName = it.split("<>")[1]
                    val defaultTime = ZaxWorld.config.getInt("World.${worldName}.time")
//                    val file = newFile(getDataFolder(), "data/${playerName}.yml", create = true)
                    val file = newFile(ZaxWorld.config.getString("YamlDataPath")!!.replace("%dataFolder%",
                        getDataFolder().path
                    )+"/${playerName}.yml", create = true)
                    val loadFromFile = Configuration.loadFromFile(file, Type.YAML)
                    loadFromFile[worldName] = dataMap[it]?:defaultTime
                    loadFromFile.saveToFile(file)
                }
            }
        }
        fun loadYamlData(){
            submitAsync(now = true, delay = 0) {
//                val file = File(getDataFolder(),"data")
                val file = File(ZaxWorld.config.getString("YamlDataPath")!!.replace("%dataFolder%", getDataFolder().path))
                if (!file.exists()){
                    file.mkdirs()
                }
                if (file.isDirectory){
                    file.listFiles()?.forEach {
                        val playerName = it.name.replace(".yml","")
                        val loadFromFile = Configuration.loadFromFile(it, Type.YAML)
                        loadFromFile.getKeys(false).forEach { world ->
                            val time = loadFromFile.getInt(world)
                            ZaxWorld.getPlayerCacheMap().set(playerName,world, time)
                        }
                    }
                }
            }
        }
    }
}