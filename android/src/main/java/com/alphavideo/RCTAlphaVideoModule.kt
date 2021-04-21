// RCTAlphaVideoModule.java
package com.alphavideo

import android.net.http.HttpResponseCache
import com.facebook.react.bridge.*
import com.alphavideo.util.CacheUtil
import java.io.File
import kotlin.math.log

class RCTAlphaVideoModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "RCTAlphaVideoModule"
    }

    /**
     * 预下载
     */
    @ReactMethod
    fun advanceDownload(urls: ReadableArray?) {
//        println("预加载 == ${urls?.size()}")
        if (urls != null && urls.size() > 0) {
            if (CacheUtil.isDir()) {
                val list = urls.toArrayList()
                for (url in list) {
                    if (url.toString().startsWith("http")) {
                        Thread(Runnable {
                            AlphaVideoParser.playVideoFromUrl(url.toString(), false) {
                                println("$url  缓存成功")
                            }
                        }).start()
                    }
                }
            } else {
                CacheUtil.onCreate(reactContext)
                advanceDownload(urls)
            }
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