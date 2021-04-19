package com.reactlibrary.util

import android.content.Context
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.Inflater

object CacheUtil {
    lateinit var cacheDir: String

    private val threadNum = AtomicInteger(0)

    internal var threadPoolExecutor = Executors.newCachedThreadPool { r ->
        Thread(r, "videoParser-Thread-${threadNum.getAndIncrement()}")
    }

    class FileDownloader {
        // 解析
        fun resume(
            url: URL,
            complete: (inputStream: InputStream) -> Unit,
            failure: (e: Exception) -> Unit
        ): () -> Unit {
            var cancelled = false
            val cancelBlock = {
                cancelled = true
            }
            threadPoolExecutor.execute {
                try {
                    println("================ video file download start ================")
                    (url.openConnection() as? HttpURLConnection)?.let {
                        it.connectTimeout = 20 * 1000
                        it.requestMethod = "GET"
                        it.connect()
                        it.inputStream.use { inputStream ->
                            ByteArrayOutputStream().use { outputStream ->
                                val buffer = ByteArray(4096)
                                var count: Int
                                while (true) {
                                    if (cancelled) {
                                        println("================ video file download canceled ================")
                                        break
                                    }
                                    count = inputStream.read(buffer, 0, 4096)
                                    if (count == -1) {
                                        break
                                    }
                                    outputStream.write(buffer, 0, count)
                                }
                                if (cancelled) {
                                    println("================ video file download canceled ================")
                                    return@execute
                                }
                                ByteArrayInputStream(outputStream.toByteArray()).use {
                                    println("================ video file download complete ================")
                                    complete(it)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("================ video file download fail ================")
                    println("error: ${e.message}")
                    e.printStackTrace()
                    failure(e)
                }
            }
            return cancelBlock
        }
    }

    var fileDownloader = FileDownloader()

    fun onCreate(context: Context) {
        cacheDir = "${context.cacheDir.absolutePath}/video/"
        File(cacheDir).takeIf { !it.exists() }?.mkdirs()
    }


    fun getCacheKey(string: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(string.toByteArray(charset("UTF-8")))
        val digest = messageDigest.digest()
        var str = ""
        for (b in digest) {
            str += String.format("%02x", b)
        }
        return str
    }

    fun isCacheKey(cacheKey: String): Boolean {
        return findVideoFile(cacheKey).exists()
    }

    fun findVideoFile(cacheKey: String): File {
        return File("${cacheDir}$cacheKey.mp4")
    }

    // 下载mp4到cache
    fun downloadVideoFromUrl(cacheKey: String, url: String, callback: ((path: String) -> Unit)?) {
        // 解析地址
        fileDownloader.resume(URL(url), { inputStream ->
            readAsBytes(inputStream)?.let { bytes ->
                threadPoolExecutor.execute {
                    findVideoFile(cacheKey).let { cacheFile ->
                        try {
                            cacheFile.takeIf { !it.exists() }?.createNewFile()
                            FileOutputStream(cacheFile).write(bytes)
                            println("FileOutputStream write")

                            if (callback != null) {
                                callback(findVideoFile(cacheKey).path)
                            }
                        } catch (e: Exception) {
                            println("create cache file fail. $e")
                            cacheFile.delete()
                        }
                    }
                }
//                inflate(bytes).let {
//                    if (callback != null) {
//                        callback(findVideoFile(cacheKey).path)
//                    }
//                }
            }

        }, {
            println("缓存失败")
        })
    }

    private fun readAsBytes(inputStream: InputStream): ByteArray? {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            val byteArray = ByteArray(2048)
            while (true) {
                val count = inputStream.read(byteArray, 0, 2048)
                if (count <= 0) {
                    break
                } else {
                    byteArrayOutputStream.write(byteArray, 0, count)
                }
            }
            return byteArrayOutputStream.toByteArray()
        }
    }

    private fun inflate(byteArray: ByteArray): ByteArray? {
        val inflater = Inflater()
        inflater.setInput(byteArray, 0, byteArray.size)
        val inflatedBytes = ByteArray(2048)
        ByteArrayOutputStream().use { inflatedOutputStream ->
            while (true) {
                val count = inflater.inflate(inflatedBytes, 0, 2048)
                if (count <= 0) {
                    break
                } else {
                    inflatedOutputStream.write(inflatedBytes, 0, count)
                }
            }
            inflater.end()
            return inflatedOutputStream.toByteArray()
        }
    }
}