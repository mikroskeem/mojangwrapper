/*
 * Copyright 2017 Mark Vainomaa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package eu.mikroskeem.mojangwrapper

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Mojang API wrapper
 *
 * @author Mark Vainomaa
 */
private val RATE_LIMIT_CODE = 429

class MojangWrapper(
        private val httpClient: OkHttpClient = OkHttpClient(),
        private val userAgent: String = "MojangWrapper/0.0.1",
        private val dataStore: DataStore = RedisDataStore(),
        private val gson: Gson = Gson()
) {
    private val log = LoggerFactory.getLogger(MojangWrapper::class.java)
    private val uuidCache: LoadingCache<String, UUID> = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build {
                TODO()
            }

    /**
     * Tries to do Username -> UUID lookup
     */
    fun resolveUUID(username: String): UUID? = resolveUUIDs(username)[0]

    /**
     * Tries to do Usernames -> UUIDs lookup
     */
    fun resolveUUIDs(usernames: List<String>): List<UUID?> = when {
        usernames.size > 100 -> {
            if(log.isTraceEnabled) log.trace("Trying to resolve {} UUIDs from usernames", usernames.size)
            val uuidList = mutableListOf<UUID?>()
            usernames.toList().batch(100).forEach {
                launch(CommonPool) {
                    if(log.isTraceEnabled) log.trace("Launching up new coroutine with username array of size {}", it.size)
                    resolveUUIDs(*it.toTypedArray()).run(uuidList::addAll)
                }
            }
            uuidList
        }
        //<editor-fold desc="2-100">
        usernames.size > 1 -> {
            if(log.isTraceEnabled) log.trace("Trying to resolve {} UUIDs from usernames", usernames.size)
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.mojang.com")
                    .addPathSegment("profiles")
                    .addPathSegment("minecraft")
                    .build()
            val request = Request.Builder()
                    .addHeader("User-Agent", userAgent)
                    .url(url)
                    .post(RequestBody.create(
                            MediaType.parse("application/json"),
                            gson.toJson(usernames)
                    ))
                    .build()
            httpClient.newCall(request).execute().use {
                return if(it.isSuccessful) {
                    val userMap = mutableMapOf<String, UUID?>().apply {
                        usernames.forEach { put(it, null) }
                    }
                    // Parse result
                    JsonParser().run {
                        val rootArray = parse(it.body()!!.charStream()).asJsonArray
                        rootArray.map { it.asJsonObject }.forEach {
                            val uuidString = it.get("id")
                            val userName = it.get("name").asString

                            if(userMap.contains(userName))
                                userMap.put(userName, uuidString.asString.convertUUID())
                        }
                    }
                    listOf(*userMap.values.toTypedArray())
                } else {
                    if(log.isDebugEnabled) {
                        if(it.code() == RATE_LIMIT_CODE) {
                            log.warn("Ratelimited by Mojang API. Failed to do Username -> UUID lookup")
                        } else {
                            log.debug("'{}' endpoint returned code '{}' and body: {}", url, it.code(), it.body())
                        }
                    }
                    listOf(*arrayOfNulls(usernames.size))
                }
            }
        }
        //</editor-fold>
        //<editor-fold desc="1 username">
        usernames.size == 1 -> {
            if(log.isTraceEnabled) log.trace("Trying to resolve single username '{}' UUID", usernames[0])
            val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("api.mojang.com")
                    .addPathSegments("users")
                    .addPathSegment("profiles")
                    .addPathSegment("minecraft")
                    .addPathSegment(usernames[0])
                    .build()
            val request = Request.Builder()
                    .addHeader("User-Agent", userAgent)
                    .url(url)
                    .get()
                    .build()
            httpClient.newCall(request).execute().use {
                if(it.isSuccessful) {
                    // 204 -> Not found
                    if(it.code() == 204) {
                        if(log.isTraceEnabled)
                            log.trace("UUID not found for username '{}'", usernames[0])
                        return listOf(*arrayOfNulls(1))
                    }

                    // Do JSON parsing
                    JsonParser().run {
                        val root = parse(it.body()!!.charStream()).asJsonObject
                        root.get("name").asString.run {
                            if(this != usernames[0]) throw IllegalStateException("$this != ${usernames[0]}")
                        }
                        // Get UUID string and convert it to UUID object
                        return listOf(root.get("id").asString.convertUUID())
                    }
                } else {
                    if(log.isDebugEnabled) {
                        if(it.code() == RATE_LIMIT_CODE) {
                            log.warn("Ratelimited by Mojang API. Failed to do Username -> UUID lookup")
                        } else {
                            log.debug("'{}' endpoint returned code '{}' and body: {}", url, it.code(), it.body())
                        }
                    }
                    return listOf(*arrayOfNulls(1))
                }
            }
        }
        //</editor-fold>
        usernames.isEmpty() -> emptyList()
        else -> throw IllegalStateException("Should not reach here!")
    }

    /**
     * Redirects to [resolveUUIDs], just uses varargs and returns array
     */
    fun resolveUUIDs(vararg usernames: String): Array<out UUID?> = resolveUUIDs(listOf(*usernames)).toTypedArray()
}

// https://stackoverflow.com/a/42305023
fun <T> List<T>.batch(chunkSize: Int): List<List<T>> {
    if (chunkSize <= 0) throw IllegalArgumentException("chunkSize must be greater than 0")
    val capacity = (size + chunkSize - 1) / chunkSize
    val list = ArrayList<ArrayList<T>>(capacity)
    for(i in 0 until size) {
        if (i % chunkSize == 0)
            list.add(ArrayList(chunkSize))
        list.last().add(this[i])
    }
    return list
}