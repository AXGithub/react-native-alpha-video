package com.reactlibrary

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactContext
import com.reactlibrary.mxVideo.MxVideoView

class AlphaVideoView(private val _activity: Activity, private val _context: ReactContext) : FrameLayout(_context) {
    private var videoView: MxVideoView
    private fun initView() {
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        videoView.layoutParams = layoutParams
        this.addView(videoView)
        videoView.setOnVideoEndedListener {
            Log.d(TAG, "onVideoEnded: 播放完成")
            videoView.release()
            closeView()
        }

        videoView.setVideoFromAssets("aa.mp4");
//        videoView.setVideoByUrl("https://cdn-streaming.onemicroworld.com/BDA30E8D-3227-FEA2-41A2-E4018E21131D.mp4?UCloudPublicKey=qgchM9CFzaKL9XWizIjY4EXmtmtDqPoFCr69qE5P&Signature=QY02GUOIZB83VHR5iQOcI8WmafA%3D");
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
        videoView = MxVideoView(_context, null)
        initView()
    }
}