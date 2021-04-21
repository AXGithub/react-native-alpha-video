package com.alphavideo

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.alphavideo.mxVideo.MxVideoView

class AlphaVideoView(private val _activity: Activity, private val _context: ReactContext) : FrameLayout(_context) {
    private var videoView: MxVideoView = MxVideoView(_context, null)
    private fun initView() {
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        videoView.layoutParams = layoutParams
        this.addView(videoView)
        videoView.setOnVideoEndedListener {
            (context as? ReactContext)?.let {
                val map = Arguments.createMap()
                it.getJSModule(RCTEventEmitter::class.java).receiveEvent(id, "onDidPlayFinish", map)
            }
        }
    }

    fun getMxVideoView(): MxVideoView {
        return videoView
    }

    fun closeView() {
        removeView(videoView)
    }

    companion object {
        private const val TAG = "AlphaVideoView"
    }

    init {
        initView()
    }

    override fun requestLayout() {
        super.requestLayout()
        removeCallbacks(measure)
        post(measure)
    }

    private val measure: Runnable = Runnable {
        measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        layout(left, top, right, bottom)
    }
}