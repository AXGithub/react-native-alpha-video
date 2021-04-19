package com.reactlibrary

import com.reactlibrary.util.CacheUtil


object AlphaVideoParser {

    /**
     * 播放远程视频
     * 预加载可以回调传null
     */
    fun playVideoFromUrl(url: String, callback: ((path: String) -> Unit)?) {
        val cacheKey = CacheUtil.getCacheKey(url)
        if (CacheUtil.isCacheKey(cacheKey)) {
            // 播放
            if (callback != null) {
                callback(CacheUtil.findVideoFile(cacheKey).path)
            }
        } else {
            CacheUtil.downloadVideoFromUrl(cacheKey, url, callback)
//            println("没有缓存,准备下载")
//            GlobalScope.launch {
//                val deferred = async() {
//                    // 下载
//                    CacheUtil.downloadVideoFromUrl(cacheKey, url, callback)
//                }
//                val res = deferred.await()
//                println("下载完成 = $res")
//                if (callback != null) {
//                    println("带播放")
//                    callback(CacheUtil.findVideoFile(cacheKey).path)
//                }
//            }
        }
    }
}