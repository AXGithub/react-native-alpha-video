package com.reactlibrary

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.reactlibrary.mxVideo.MxVideoView

class AlphaVideoView(private val _activity: Activity, private val _context: ReactContext) : FrameLayout(_context) {
    private var videoView: MxVideoView = MxVideoView(_context, null)
    private fun initView() {
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        videoView.layoutParams = layoutParams
        this.addView(videoView)
        videoView.setOnVideoEndedListener {
            println("onVideoEnded: 播放完成")
            (context as? ReactContext)?.let {
                val map = Arguments.createMap()
                it.getJSModule(RCTEventEmitter::class.java).receiveEvent(id, "onDidPlayFinish", map)
            }
            videoView.release()
            closeView()
        }
    }

    fun getMxVideoView(): MxVideoView {
        return videoView
    }

    private fun closeView() {
        removeView(videoView)
    }

    companion object {
        private const val TAG = "AlphaVideoView"
    }

    init {
        initView()
    }
}