package io.github.yuazer.zaxworld.database

import io.github.yuazer.zaxworld.ZaxWorld
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class DataSQL {
    private val host = ZaxWorld.databaseConfig.getString("database.host")
    private val port = ZaxWorld.databaseConfig.getString("database.port")
    private val user = ZaxWorld.databaseConfig.getString("database.user")
    private val password = ZaxWorld.databaseConfig.getString("database.password")
    private val name = ZaxWorld.databaseConfig.getString("database.database")
    private val url = "jdbc:mysql://${host}:${port}/${name}?useSSL=false"
    private val serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true"
    fun createDatabaseIfNotExists(serverUrl: String, username: String, password: String, databaseName: String) {
        var connection: Connection? = null
        try {
            // 连接到MySQL服务器
            connection = DriverManager.getConnection(serverUrl, username, password)
            val statement = connection.createStatement()
            // 执行SQL语句，检查数据库是否存在，如果不存在则创建
            val sql = "CREATE DATABASE IF NOT EXISTS $databaseName"
            statement.executeUpdate(sql)
            println("数据库 $databaseName 已经成功创建或已存在。")
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            // 确保数据库连接被正确关闭
            try {
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
    fun createTableIfNotExists(databaseUrl: String, username: String, password: String, tableName: String) {
        var connection: Connection? = null
        try {
            // 连接到指定的数据库
            connection = DriverManager.getConnection(databaseUrl, username, password)
            val statement = connection.createStatement()

            // 检查表是否存在，如果不存在则创建
            val sql = """
            CREATE TABLE IF NOT EXISTS $tableName (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user VARCHAR(255) NOT NULL,
                worldName VARCHAR(255) NOT NULL,
                time INT NOT NULL
            )
        """.trimIndent()
            statement.executeUpdate(sql)

            println("表 $tableName 已经成功创建或已存在。")
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            // 确保数据库连接被正确关闭
            try {
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
    init {
        createDatabaseIfNotExists(serverUrl,user?:"root",password?:"root",name?:"zaxworld")
        createTableIfNotExists(url,user?:"root",password?:"root","player_data")
    }

    private fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(url, user, password)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun addUser(user: String, worldName: String, time: Int) {
        getConnection()?.use { conn ->
            val stmt = conn.prepareStatement("INSERT INTO player_data (user, worldName, time) VALUES (?, ?, ?)")
            stmt.setString(1, user)
            stmt.setString(2, worldName)
            stmt.setInt(3, time)
            stmt.executeUpdate()
        }
    }

    fun deleteUser(user: String) {
        getConnection()?.use { conn ->
            val stmt = conn.prepareStatement("DELETE FROM player_data WHERE user = ?")
            stmt.setString(1, user)
            stmt.executeUpdate()
        }
    }

    fun upsertUserWorld(user: String, worldName: String, time: Int) {
        getConnection()?.use { conn ->
            // 首先尝试更新存在的记录
            val updateStmt = conn.prepareStatement("UPDATE player_data SET time = ? WHERE user = ? AND worldName = ?")
            updateStmt.setInt(1, time)
            updateStmt.setString(2, user)
            updateStmt.setString(3, worldName)
            val updatedRows = updateStmt.executeUpdate()

            // 如果没有记录被更新（意味着不存在相同的user和worldName组合），则插入新记录
            if (updatedRows == 0) {
                val insertStmt = conn.prepareStatement("INSERT INTO player_data (user, worldName, time) VALUES (?, ?, ?)")
                insertStmt.setString(1, user)
                insertStmt.setString(2, worldName)
                insertStmt.setInt(3, time)
                insertStmt.executeUpdate()
            }
        }
    }
    fun upsertPlayerData(user: String, worldName: String, time: Int) {
        getConnection()?.use {
            connection: Connection ->
            try {
                // 尝试更新记录
                val updateSql = "UPDATE player_data SET time = ? WHERE user = ? AND worldName = ?"
                val updateStmt: PreparedStatement = connection.prepareStatement(updateSql).apply {
                    setInt(1, time)
                    setString(2, user)
                    setString(3, worldName)
                }
                val updatedRows = updateStmt.executeUpdate()
                // 如果没有记录被更新，则插入新记录
                if (updatedRows == 0) {
                    val insertSql = "INSERT INTO player_data (user, worldName, time) VALUES (?, ?, ?)"
                    val insertStmt: PreparedStatement = connection.prepareStatement(insertSql).apply {
                        setString(1, user)
                        setString(2, worldName)
                        setInt(3, time)
                    }

                    insertStmt.executeUpdate()
                }
                println("数据更新成功")
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        }

    }

    fun queryUserWorldsAndTimes(user: String): List<Map<String, Int>> {
        val results = mutableListOf<Map<String, Int>>()
        getConnection()?.use { conn ->
            val stmt = conn.prepareStatement("SELECT worldName, time FROM player_data WHERE user = ?")
            stmt.setString(1, user)
            val resultSet = stmt.executeQuery()
            while (resultSet.next()) {
                val worldName = resultSet.getString("worldName")
                val time = resultSet.getInt("time")
                results.add(mapOf(worldName to time))
            }
        }
        return results // 返回一个包含Map的List
    }
    
    fun listAllUsers(): Set<String> {
        val users = mutableSetOf<String>()
        getConnection()?.use { conn ->
            val stmt = conn.prepareStatement("SELECT DISTINCT user FROM player_data")
            val resultSet = stmt.executeQuery()
            while (resultSet.next()) {
                users.add(resultSet.getString("user"))
            }
        }
        return users // 直接返回users，而不是在use块中返回
    }
    fun getUserWorldsAndTimes(user: String): MutableMap<String, Int> {
        val worldTimes = mutableMapOf<String, Int>()
        getConnection()?.use { conn ->
            val stmt = conn.prepareStatement("SELECT worldName, time FROM player_data WHERE user = ?")
            stmt.setString(1, user)
            val resultSet = stmt.executeQuery()
            while (resultSet.next()) {
                val worldName = resultSet.getString("worldName")
                val time = resultSet.getInt("time")
                worldTimes[worldName] = time
            }
        }
        return worldTimes
    }

}