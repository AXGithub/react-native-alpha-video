// RCTAlphaVideoModule.java
package com.reactlibrary

import android.net.http.HttpResponseCache
import com.facebook.react.bridge.*
import com.reactlibrary.util.CacheUtil
import java.io.File

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
                    AlphaVideoParser.playVideoFromUrl(list[index].toString(), false) {
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

    @ReactMethod
    fun pause() {
        RCTAlphaVideoManager.videoView.getMxVideoView().pause()
    }

    @ReactMethod
    fun play() {
        RCTAlphaVideoManager.videoView.getMxVideoView().start()
    }

    @ReactMethod
    fun stop() {
        RCTAlphaVideoManager.videoView.getMxVideoView().stop()
    }

    @ReactMethod
    fun clear() {
        RCTAlphaVideoManager.videoView.getMxVideoView().release()
        RCTAlphaVideoManager.videoView.closeView()
    }

    init {
        val cacheDir = File(reactContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
        CacheUtil.onCreate(reactContext)
    }
}