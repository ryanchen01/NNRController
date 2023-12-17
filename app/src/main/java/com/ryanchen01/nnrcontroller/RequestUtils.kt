package com.ryanchen01.nnrcontroller

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

fun refreshServerContent(context: Context): Int {

    val dbHelper = DatabaseHelper(context)
    val token = dbHelper.getToken()

    // Create a json object for a HTTP POST request
    val json = JSONObject()
    //json.put("key", "value")
    val url = "https://nnr.moe/api/servers" // Replace with your actual URL
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = json.toString().toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url) // Replace with your URL
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("token", token) // Replace with your actual token
        .build()

    // Send the HTTP POST request to https://nnr.moe/api/servers
    val client = OkHttpClient()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Process the response
                    // If the response is successful, read the JSON data containing an array of JSON objects
                    val jsonData = response.body?.string() ?: "{\"status\":0}"
                    val jsonResponse = JSONObject(jsonData)
                    if (jsonResponse.getInt("status") != 1) {
                        return
                    }
                    val jsonArray = jsonResponse.getJSONArray("data")
                    var servers = mutableListOf<String>()
                    // Iterate through the JSON array
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        servers.add(jsonObject.toString())
                    }
                    dbHelper.updateServers(servers)

                }
            }
        })
    } catch (e: Exception) {
        // Handle failure
        return 0
    }
    return 1

}

fun refreshRuleContent(context: Context): Int {

    val dbHelper = DatabaseHelper(context)
    val token = dbHelper.getToken()

    // Create a json object for a HTTP POST request
    val json = JSONObject()
    //json.put("key", "value")
    val url = "https://nnr.moe/api/rules/" // Replace with your actual URL
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = json.toString().toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url) // Replace with your URL
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("token", token) // Replace with your actual token
        .build()

    // Send the HTTP POST request to https://nnr.moe/api/servers
    val client = OkHttpClient()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Process the response
                    // If the response is successful, read the JSON data containing an array of JSON objects
                    val jsonData = response.body?.string() ?: "{\"status\":0}"
                    val jsonResponse = JSONObject(jsonData)
                    if (jsonResponse.getInt("status") != 1) {
                        return
                    }
                    val jsonArray = jsonResponse.getJSONArray("data")
                    var hosts = mutableListOf<String>()
                    // Iterate through the JSON array
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val host = jsonObject.getString("host")

                        hosts.add(jsonObject.toString())
                    }
                    dbHelper.updateRules(hosts)

                }
            }
        })
    } catch (e: Exception) {
        // Handle failure
        return 0
    }
    return 1
}

fun editRule(context: Context, ruleObject: JSONObject): Int {

    val dbHelper = DatabaseHelper(context)
    val token = dbHelper.getToken()

    // Create a json object for a HTTP POST request
    val json = JSONObject()
    json.put("rid", ruleObject.getString("rid"))
    json.put("remote", ruleObject.getString("remote"))
    json.put("rport", ruleObject.getString("rport"))
    json.put("type", ruleObject.getString("type"))
    json.put("name", ruleObject.getString("name"))
    json.put("setting", "{\"loadbalanceMode\": \"fallback\"}")
    val url = "https://nnr.moe/api/rules/edit" // Replace with your actual URL
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = json.toString().toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url) // Replace with your URL
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("token", token) // Replace with your actual token
        .build()

    // Send the HTTP POST request to https://nnr.moe/api/servers
    val client = OkHttpClient()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Process the response
                    refreshRuleContent(context)

                }
            }
        })
    } catch (e: Exception) {
        // Handle failure
        return 0
    }
    return 1
}

fun deleteRule(context: Context, ruleObject: JSONObject): Int {

    val dbHelper = DatabaseHelper(context)
    val token = dbHelper.getToken()

    // Create a json object for a HTTP POST request
    val json = JSONObject()
    json.put("rid", ruleObject.getString("rid"))
    val url = "https://nnr.moe/api/rules/del" // Replace with your actual URL
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = json.toString().toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url) // Replace with your URL
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("token", token) // Replace with your actual token
        .build()

    // Send the HTTP POST request to https://nnr.moe/api/servers
    val client = OkHttpClient()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Process the response
                    refreshRuleContent(context)

                }
            }
        })
    } catch (e: Exception) {
        // Handle failure
        return 0
    }
    return 1
}

fun addRule(context: Context, ruleObject: JSONObject): Int {

    val dbHelper = DatabaseHelper(context)
    val token = dbHelper.getToken()

    val url = "https://nnr.moe/api/rules/add" // Replace with your actual URL
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = ruleObject.toString().toRequestBody(mediaType)
    val request = Request.Builder()
        .url(url) // Replace with your URL
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("token", token) // Replace with your actual token
        .build()

    // Send the HTTP POST request to https://nnr.moe/api/servers
    val client = OkHttpClient()
    try {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Process the response
                    refreshRuleContent(context)

                }
            }
        })
    } catch (e: Exception) {
        // Handle failure
        return 0
    }
    return 1
}

