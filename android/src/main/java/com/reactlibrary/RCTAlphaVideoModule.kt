// RCTAlphaVideoModule.java
package com.reactlibrary

import android.net.http.HttpResponseCache
import com.facebook.react.bridge.*
import java.io.File
import java.net.URL

class RCTAlphaVideoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "RCTAlphaVideoModule"
    }

    /**
     * 预下载
     */
    @ReactMethod
    fun advanceDownload(urls: ReadableArray?) {
        if (urls != null && urls.size() > 0) {
            val list = urls.toArrayList()

            fun downloadUrl(index: Int) {
                if (index < list.size) {
                    println("正在缓存 $index ${list.lastIndex} ${list[index]}")
                    AlphaVideoParser.playVideoFromUrl(list[index].toString()) {
                        println("第 $index 个 ${list[index]} 缓存成功")
                        downloadUrl(index + 1)
                    }
                }
            }

            Thread(Runnable {
                downloadUrl(0)
            }).start()
        }
    }

    init {
        val cacheDir = File(reactContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
    }
}