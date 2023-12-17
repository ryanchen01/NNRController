package com.ryanchen01.nnrcontroller

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_data.db" // Database name
        private const val DATABASE_VERSION = 1 // Database version
    }

    override fun onCreate(db: SQLiteDatabase) {
        // SQL statement to create a table
        val createUserInfoTableStatement = "CREATE TABLE IF NOT EXISTS user_info (token TEXT)"
        db.execSQL(createUserInfoTableStatement)
        val createRuleTableStatement = "CREATE TABLE IF NOT EXISTS rules (rule TEXT)"
        db.execSQL(createRuleTableStatement)
        val createServerTableStatement = "CREATE TABLE IF NOT EXISTS servers (server TEXT)"
        db.execSQL(createServerTableStatement)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Code to upgrade the database if the schema changes in future versions
        db.execSQL("DROP TABLE IF EXISTS user_info")
        db.execSQL("DROP TABLE IF EXISTS rules")
        db.execSQL("DROP TABLE IF EXISTS servers")
        onCreate(db)
    }

    fun getToken(): String {
        val db = this.readableDatabase
        var token = ""
        val cursor = db.rawQuery("SELECT token FROM user_info LIMIT 1", null)

        if (cursor.moveToFirst()) {
            token = cursor.getString(0)
        }
        cursor.close()
        return token
    }

    fun updateToken(newToken: String) {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM user_info LIMIT 1", null)
        if (cursor.moveToFirst()) {
            val contentValues = ContentValues()
            contentValues.put("token", newToken)
            db.update("user_info", contentValues, null, null)
        } else {
            val contentValues = ContentValues()
            contentValues.put("token", newToken)
            db.insert("user_info", null, contentValues)
        }
        cursor.close()
    }

    fun getRules(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT rule FROM rules", null)
        val rules = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                rules.add(cursor.getString(0))
            } while (cursor.moveToNext())
        } else {
            rules.add("None")
        }
        cursor.close()
        return rules
    }

    fun getRule(rid: String): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT rule FROM rules", null)
        var rule = ""
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject(cursor.getString(0))
                if (jsonObject.getString("rid") == rid) {
                    rule = cursor.getString(0)
                }
            } while (cursor.moveToNext())
        } else {
            rule = "None"
        }
        cursor.close()
        return rule
    }

    fun updateRules(newRules: List<String>) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM rules")
        for (rule in newRules) {
            val contentValues = ContentValues()
            contentValues.put("rule", rule)
            db.insert("rules", null, contentValues)
        }
    }

    fun updateServers(newServers: List<String>) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM servers")
        for (server in newServers) {
            val contentValues = ContentValues()
            contentValues.put("server", server)
            db.insert("servers", null, contentValues)
        }
    }


    fun getServers(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT server FROM servers", null)
        val servers = mutableListOf<String>()
        if (cursor.moveToFirst()) {
            do {
                servers.add(cursor.getString(0))
            } while (cursor.moveToNext())
        } else {
            servers.add("None")
        }
        cursor.close()
        return servers
    }

    fun getServerByName(serverName: String): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT server FROM servers", null)
        var server = ""
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject(cursor.getString(0))
                if (jsonObject.getString("name") == serverName) {
                    server = cursor.getString(0)
                    break
                }
            } while (cursor.moveToNext())
        } else {
            server = "None"
        }
        cursor.close()
        return server
    }

    fun getServerBySID(sid: String): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT server FROM servers", null)
        var server = ""
        if (cursor.moveToFirst()) {
            do {
                val jsonObject = JSONObject(cursor.getString(0))
                if (jsonObject.getString("sid") == sid) {
                    server = cursor.getString(0)
                    break
                }
            } while (cursor.moveToNext())
        } else {
            server = "None"
        }
        cursor.close()
        return server
    }
}