package io.github.yuazer.zaxworld.utils

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.platform.compat.replacePlaceholder

object PlayerUtils {
    fun runKether(script: List<String>, player: Player) {
        taboolib.module.kether.runKether(script,detailError = true) {
            KetherShell.eval(
                script.replacePlaceholder(player), options = ScriptOptions(
                    sender = adaptCommandSender(player)
                )
            )
        }
    }
    fun replaceInList(strings: List<String>, oldChar: String, newChar: String): List<String> {
        return strings.map { it.replace(oldChar, newChar) }
    }
}