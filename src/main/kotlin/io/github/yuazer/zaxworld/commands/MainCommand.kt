package io.github.yuazer.zaxworld.commands

import io.github.yuazer.zaxworld.ZaxWorld
import io.github.yuazer.zaxworld.api.ZaxWorldAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.sendLang

@CommandHeader("zaxworld", ["zw"], permissionDefault = PermissionDefault.TRUE)
object MainCommand {
    // 创建扩展函数简化消息替换

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
                            sender.sendLang("already-free-player")
                        } else {
                            ZaxWorld.getPlayerFreeSet().add(user)
                        }
                    } else if (operate.equals("del", true)) {
                        if (!ZaxWorldAPI.isFree(user)) {
                            sender.sendLang("already-not-free-player")
                        } else {
                            ZaxWorld.getPlayerFreeSet().remove(user)
                        }
                    } else {
                        sender.sendLang("error-command-format")
                    }
                }
            }
            execute<CommandSender> { sender, context, argument ->
                val operate = context["add/del"]
                if (sender is Player) {
                    val user: String = sender.name
                    if (operate.equals("add", true)) {
                        if (ZaxWorldAPI.isFree(user)) {
                            sender.sendLang("already-free-player")
                        } else {
                            ZaxWorld.getPlayerFreeSet().add(user)
                        }
                    } else if (operate.equals("del", true)) {
                        if (!ZaxWorldAPI.isFree(user)) {
                            sender.sendLang("already-not-free-player")
                        } else {
                            ZaxWorld.getPlayerFreeSet().remove(user)
                        }
                    } else {
                        sender.sendLang("error-command-format")
                    }
                } else {
                    sender.sendLang("only-player-command")
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
                    sender.sendLang("manage-command-message-add", time, user.name, worldName)
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
                        sender.sendLang("manage-command-message-add", time, user, worldName)
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
                    sender.sendLang("manage-command-message-reduce", time, user.name, worldName)
                }
            }
            dynamic("world") {
                dynamic("time") {
                    execute<CommandSender> { sender, context, argument ->
                        val user = context["user"]
                        val worldName: String = context["world"]
                        val time: Int = context.int("time")
                        ZaxWorld.getPlayerCacheMap().reduce(user, worldName, time)
                        sender.sendLang("manage-command-message-reduce", time, user, worldName)
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
                    sender.sendLang("manage-command-message-set", time, user.name, worldName)
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
                        sender.sendLang("manage-command-message-set", time, user, worldName)
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
                    sender.sendLang("manage-command-message-check", user, worldName, time)
                }
            }
        }
        execute<CommandSender> { sender, context, argument ->
            if (sender is Player) {
                val user: Player = sender
                val worldName: String = user.world.name
                val time = ZaxWorld.getPlayerCacheMap().getPlayerWorldTime(user.name, worldName)
                sender.sendLang("manage-command-message-check", user.name, worldName, time)
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
