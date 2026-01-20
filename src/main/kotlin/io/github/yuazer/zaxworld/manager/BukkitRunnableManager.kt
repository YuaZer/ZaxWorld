package io.github.yuazer.zaxworld.manager

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class BukkitRunnableManager(private val plugin: JavaPlugin) {
    private val tasks: MutableMap<String, BukkitRunnable> = mutableMapOf()
    fun add(key: String, runnable: BukkitRunnable) {
        // 添加任务到管理器，不立即启动
        tasks[key]?.cancel() // 如果有相同的任务正在运行，则先停止它
        tasks[key] = runnable
    }

    fun add(playerName: String, worldName: String, runnable: BukkitRunnable) {
        val key = ZaxWorld.getPlayerCacheMap().getPWKey(playerName, worldName)
        tasks[key]?.cancel() // 如果有相同的任务正在运行，则先停止它
        tasks[key] = runnable
    }

    fun syncStart(playerName: String, worldName: String, delay: Long = 0L, period: Long = 20L) {
        val key = ZaxWorld.getPlayerCacheMap().getPWKey(playerName, worldName)
        tasks[key]?.runTaskTimer(plugin, delay, period)
    }

    fun asyncStart(playerName: String, worldName: String, delay: Long = 0L, period: Long = 20L) {
        val key = ZaxWorld.getPlayerCacheMap().getPWKey(playerName, worldName)
        tasks[key]?.runTaskTimerAsynchronously(plugin, delay, period)
    }

    fun stop(key: String) {
        if (tasks[key]?.isCancelled == true){
            tasks.remove(key)
            return
        }
        tasks[key]?.cancel()
        tasks.remove(key)
    }
    fun stop(playerName: String, worldName: String) {
        val key = ZaxWorld.getPlayerCacheMap().getPWKey(playerName, worldName)
        val task = tasks[key]
        try {
            task?.cancel()
        } catch (e: IllegalStateException) {
            if (!e.message.orEmpty().contains("Not scheduled yet")) {
                e.printStackTrace()
            }
        }
        tasks.remove(key)
    }


    fun stopAll() {
        tasks.keys.toList().forEach(this::stop) // 防止ConcurrentModificationException
    }
}
