package io.github.yuazer.zaxworld.listener

import io.github.yuazer.zaxworld.ZaxWorld
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

object LangEvent {
    @SubscribeEvent
    fun lang(event: PlayerSelectLocaleEvent) {
        event.locale = ZaxWorld.config.getString("Lang", "zh_CN")!!
    }

    @SubscribeEvent
    fun lang(event: SystemSelectLocaleEvent) {
        event.locale = ZaxWorld.config.getString("Lang", "zh_CN")!!
    }
}