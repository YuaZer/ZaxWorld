package io.github.yuazer.zaxworld.commands

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang

@CommandHeader("zaxworld", ["zw"])
object MainCommand {
    // 创建扩展函数简化消息替换
    fun CommandSender.sendLangWithReplace(key: String, vararg pairs: Pair<String, Any>) {
        val message = asLangText(key)
        sendMessage(pairs.fold(message) { acc, (k, v) -> acc.replace("{$k}", v.toString()) })
    }

    @CommandBody(permission = "zaxworld.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            ZaxWorld.config.reload()
            sender.sendLang("reload-message")
        }
    }

    @CommandBody
    val help = subCommand {
        createHelper(true)
    }

    @CommandBody(permission = "zaxworld.free")
    val free = subCommand {
        dynamic("add/del") {
            dynamic("user") {
                execute<CommandSender> { sender, context, argument ->
                    val operate = context["add/del"]
                    val user = context["user"]
                    if (operate.equals("add", true)) {
                        if (ZaxWorldAPI.isFree(user)) {
                            sender.sendLangWithReplace(sender.asLangText("already-free-player"))
                        } else {
                            ZaxWorld.getPlayerFreeSet().add(user)
                        }
                    } else if (operate.equals("del", true)) {
                        if (!ZaxWorldAPI.isFree(user)) {
                            sender.sendLangWithReplace(sender.asLangText("already-not-free-player"))
                        } else {
                            ZaxWorld.getPlayerFreeSet().remove(user)
                        }
                    } else {
                        sender.sendLangWithReplace(sender.asLangText("error-command-format"))
                    }
                }
            }
            execute<CommandSender> { sender, context, argument ->
                val operate = context["add/del"]
                if (sender is Player) {
                    val user: String = sender.name
                    if (operate.equals("add", true)) {
                        if (ZaxWorldAPI.isFree(user)) {
                            sender.sendLangWithReplace(sender.asLangText("already-free-player"))
                        } else {
                            ZaxWorld.getPlayerFreeSet().add(user)
                        }
                    } else if (operate.equals("del", true)) {
                        if (!ZaxWorldAPI.isFree(user)) {
                            sender.sendLangWithReplace(sender.asLangText("already-not-free-player"))
                        } else {
                            ZaxWorld.getPlayerFreeSet().remove(user)
                        }
                    } else {
                        sender.sendLangWithReplace(sender.asLangText("error-command-format"))
                    }
                } else {
                    sender.sendLangWithReplace(sender.asLangText("only-player-command"))
                }
            }
        }
    }

    @CommandBody(permission = "zaxworld.add")
    val add = subCommand {
        dynamic("user") {
            execute<CommandSender> { sender, context, argument ->
                // 获取参数的值
                if (sender is Player) {
                    val user: Player = sender
                    val worldName: String = user.world.name
                    val time: Int = context.int("user")
                    ZaxWorld.getPlayerCacheMap().add(sender.name, worldName, time)
                    sender.sendLangWithReplace(
                        "manage-command-message-add",
                        "{user}" to user.name,
                        "{world}" to worldName,
                        "{time}" to time.toString()
                    )
                }
            }
            dynamic("world") {
                dynamic("time") {
                    execute<CommandSender> { sender, context, argument ->
                        // 获取参数的值
                        val user = context["user"]
                        val worldName: String = context["world"]
                        val time: Int = context.int("time")
                        ZaxWorld.getPlayerCacheMap().add(user, worldName, time)
                        sender.sendLangWithReplace(
                            "manage-command-message-add",
                            "{user}" to user,
                            "{world}" to worldName,
                            "{time}" to time.toString()
                        )
                    }
                }
            }
        }
    }

    @CommandBody(permission = "zaxworld.reduce")
    val reduce = subCommand {
        dynamic("user") {
            execute<CommandSender> { sender, context, argument ->
                // 获取参数的值
                if (sender is Player) {
                    val user: Player = sender
                    val worldName: String = user.world.name
                    val time: Int = context.int("user")
                    ZaxWorld.getPlayerCacheMap().reduce(sender.name, worldName, time)
                    sender.sendLangWithReplace(
                        "manage-command-message-reduce",
                        "{user}" to user.name,
                        "{world}" to worldName,
                        "{time}" to time.toString()
                    )
                }
            }
            dynamic("world") {
                dynamic("time") {
                    execute<CommandSender> { sender, context, argument ->
                        val user = context["user"]
                        val worldName: String = context["world"]
                        val time: Int = context.int("time")
                        ZaxWorld.getPlayerCacheMap().reduce(user, worldName, time)
                        sender.sendLangWithReplace(
                            "manage-command-message-reduce",
                            "{user}" to user,
                            "{world}" to worldName,
                            "{time}" to time.toString()
                        )
                    }
                }
            }
        }
    }

    @CommandBody(permission = "zaxworld.set")
    val set = subCommand {
        dynamic("user") {
            execute<CommandSender> { sender, context, argument ->
                if (sender is Player) {
                    val user: Player = sender
                    val worldName: String = user.world.name
                    val time: Int = context.int("user")
                    ZaxWorld.getPlayerCacheMap().set(sender.name, worldName, time)
                    sender.sendLangWithReplace(
                        "manage-command-message-set",
                        "{user}" to user.name,
                        "{world}" to worldName,
                        "{time}" to time.toString()
                    )
                }
            }
            dynamic("world") {
                dynamic("time") {
                    execute<CommandSender> { sender, context, argument ->
                        // 获取参数的值
                        val user = context["user"]
                        val worldName: String = context["world"]
                        val time: Int = context.int("time")
                        ZaxWorld.getPlayerCacheMap().set(user, worldName, time)
                        sender.sendLangWithReplace(
                            "manage-command-message-set",
                            "{user}" to user,
                            "{world}" to worldName,
                            "{time}" to time.toString()
                        )
                    }
                }
            }
        }
//        dynamic ("time"){
//            execute<CommandSender> { sender, context, argument ->
//                if (sender is Player){
//                    val user:Player = sender
//                    val worldName:String = user.world.name
//                    val time:Int = context.int("time")
//                    ZaxWorld.getPlayerCacheMap().set(sender.name,worldName, time)
//                    sender.sendMessage(sender.asLangText("manage-command-message-set")
//                        .replace("{user}",user.name)
//                        .replace("{world}",worldName)
//                        .replace("{time}",time.toString()))
//                }
//            }
//        }
    }

    @CommandBody(permission = "zaxworld.check")
    val check = subCommand {
        dynamic("user") {
            dynamic("world") {
                execute<CommandSender> { sender, context, argument ->
                    // 获取参数的值
                    val user = context["user"]
                    val worldName: String = context["world"]
                    val time = ZaxWorld.getPlayerCacheMap().getPlayerWorldTime(user, worldName)
                    sender.sendMessage(
                        sender.asLangText("manage-command-message-check")
                            .replace("{user}", user)
                            .replace("{world}", worldName)
                            .replace("{time}", time.toString())
                    )
                }
            }
        }
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                val user: Player = sender
                val worldName: String = user.world.name
                val time = ZaxWorld.getPlayerCacheMap().getPlayerWorldTime(user.name, worldName)
                sender.sendMessage(
                    sender.asLangText("manage-command-message-check")
                        .replace("{user}", user.name)
                        .replace("{world}", worldName)
                        .replace("{time}", time.toString())
                )
            }
        }
    }

    @CommandBody(permission = "zaxworld.runCheck")
    val runCheck = subCommand {
        dynamic("user") {
            execute<CommandSender> { sender, context, argument ->
                // 获取参数的值
                val user = context.player("user")
                // 转化为Bukkit的Player
                val bukkitPlayer = user.castSafely<Player>()
                if (bukkitPlayer != null) {
                    ZaxWorldAPI.checkPlayer(bukkitPlayer)
                } else {
                    sender.sendMessage("§c玩家为空!")
                }
            }
        }
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                val user: Player = sender
                ZaxWorldAPI.checkPlayer(user)
            }
        }
    }
}