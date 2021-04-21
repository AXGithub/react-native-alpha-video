package com.alphavideo

import com.alphavideo.util.CacheUtil


object AlphaVideoParser {

    /**
     * 播放远程视频
     * 预加载可以回调传null
     */
    fun playVideoFromUrl(url: String, isPlay: Boolean, callback: (path: String) -> Unit) {
        val cacheKey = CacheUtil.getCacheKey(url)
        if (CacheUtil.isCacheKey(cacheKey)) {
            // 播放
            if (isPlay) {
                callback(CacheUtil.findVideoFile(cacheKey).path)
            } else {
                callback("已缓存")
            }
        } else {
            CacheUtil.downloadVideoFromUrl(cacheKey, url, callback)
        }
    }

}