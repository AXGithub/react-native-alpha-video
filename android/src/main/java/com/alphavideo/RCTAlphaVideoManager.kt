// RCTAlphaVideoManager.java
package com.alphavideo

import android.util.Log
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class RCTAlphaVideoManager : SimpleViewManager<AlphaVideoView>() {
    lateinit var context: ThemedReactContext

    // 事件名,这里写个enum方便循环
    enum class Events(private val mName: String) {
        //        EVENT_CODE_TYPES("codeTypes"),
        ON_DID_PLAY_FINISH("onDidPlayFinish");

        override fun toString(): String {
            return mName
        }
    }

    /**
     * 设置别名
     *
     * @return
     */
    override fun getName(): String {
        return REACT_CLASS
    }

    /**
     * 初始化入口
     *
     * @param context
     * @return
     */
    override fun createViewInstance(context: ThemedReactContext): AlphaVideoView {
        val activity = context.currentActivity
        this.context = context
        videoView = AlphaVideoView(activity!!, context)
        Log.d(TAG, "createViewInstance: 初始化入口")
        return videoView
    }

    @ReactProp(name = "source")
    fun setSource(view: AlphaVideoView, source: String) {
        if (source.startsWith("http")) {
            AlphaVideoParser.playVideoFromUrl(source, true) {
                videoView.getMxVideoView().setVideoFromSD(it)
            }
        } else {
            val resid = context.resources.getIdentifier(source, "raw", context.packageName)
            val afd = context.resources.openRawResourceFd(resid)
            videoView.getMxVideoView().setVideoFromFile(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
        }
    }

    @ReactProp(name = "loop", defaultBoolean = false)
    fun setLoop(view: AlphaVideoView, isLoops: Boolean) {
        view.getMxVideoView().setLooping(isLoops)
    }

    /**
     * 注册事件
     *
     * @return
     */
    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
        val builder = MapBuilder.builder<String, Any>()
        for (event in Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()))
        }
        return builder.build()
    }

    companion object {
        private const val TAG = "RCTAlphaVideoManager"
        const val REACT_CLASS = "RNAlphaVideo"

        lateinit var videoView: AlphaVideoView

    }
}