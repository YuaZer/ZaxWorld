package io.github.yuazer.zaxworld

import io.github.yuazer.zaxworld.database.DataLoader
import io.github.yuazer.zaxworld.database.YamlData
import io.github.yuazer.zaxworld.manager.BukkitRunnableManager
import io.github.yuazer.zaxworld.mymap.PlayerWorldMap
import taboolib.common.io.newFile
import taboolib.common.platform.Platform
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.platform.BukkitPlugin

object ZaxWorld : Plugin() {
    //初始化runnable管理器
    lateinit var runnableManager: BukkitRunnableManager
    //初始化player-world-time的Map
    private lateinit var playerWorldCache: PlayerWorldMap
    fun getPlayerCacheMap(): PlayerWorldMap {
        return playerWorldCache
    }

    @Config("config.yml")
    lateinit var config: ConfigFile

    @Config("database.yml")
    lateinit var databaseConfig: ConfigFile
    @Config("freeplayer.yml")
    lateinit var freePlayer: ConfigFile
    private lateinit var freePlayerSet: MutableSet<String>
    fun getPlayerFreeSet(): MutableSet<String> {
        return freePlayerSet
    }
    override fun onEnable() {
        //初始化缓存，并从数据库中读取数据加载进缓存
        playerWorldCache = PlayerWorldMap
        //初始化Runnable管理器
        runnableManager = BukkitRunnableManager(BukkitPlugin.getInstance())
        //初始化免计时玩家
        freePlayerSet = mutableSetOf()
        freePlayer.getStringList("free").forEach {
            freePlayerSet.add(it)
        }
        if (config.getString("dataMode").equals("YAML", true)) {
            YamlData.loadYamlData()
        } else {
            //初始化数据库
            DataLoader.loadData2Cache_Async()
        }
        info("Successfully running ZaxWorld!")
        info("Author: YuaZer[QQ:1109132]")
        val dataMode = config.getString("dataMode")
        info("data mode is $dataMode")
        val timer = config.getInt("dataSaveTimer").toLong()
        submitAsync(now = false, delay = 20*60L, period = timer) {
            saveData()
        }
        info("auto save loaded, timer: $timer seconds")
        val pluginId = 24716
//        val metrics = Metrics(BukkitPlugin.getInstance(), pluginId)
        // Optional: Add custom charts
//        metrics.addCustomChart(SimplePie("ZaxPlugins") { "Powered by YuaZer" })
        val metrics =
            taboolib.module.metrics.Metrics(pluginId, BukkitPlugin.getInstance().description.version, Platform.BUKKIT)
    }

    override fun onDisable() {
        //取消所有调度器
        runnableManager.stopAll()
        //TODO 缓存数据保存到文件
        saveData()
    }
    private fun saveData(){
        if (config.getString("dataMode").equals("YAML", true)) {
            YamlData.saveYamlData()
        } else {
            DataLoader.saveDataFromCache_Async()
        }
        val freeFile = newFile(getDataFolder(),"freeplayer.yml",true)
        val loadFromFreeFile = Configuration.loadFromFile(freeFile, Type.YAML)
        loadFromFreeFile["free"] = freePlayerSet
        loadFromFreeFile.saveToFile(freeFile)
    }
}