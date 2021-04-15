package com.reactlibrary;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContext;
import com.reactlibrary.mxVideo.MxVideoView;

public class AlphaVideoView extends FrameLayout {
    private static final String TAG = "AlphaVideoView";
    private final Activity _activity;
    private final ReactContext _context;

    public MxVideoView videoView;

    public AlphaVideoView(Activity activity, @NonNull ReactContext context) {
        super(context);
        this._activity = activity;
        this._context = context;
        videoView = new MxVideoView(context, null);


        initView();
    }

    private void initView() {

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        this.addView(videoView);

        videoView.setOnVideoEndedListener(new MxVideoView.OnVideoEndedListener() {
            @Override
            public void onVideoEnded() {
                Log.d(TAG, "onVideoEnded: 播放完成");
                videoView.release();
                closeView();
            }
        });

        videoView.setVideoFromAssets("aa.mp4");
    }

    private void closeView() {
        this.removeView(videoView);
    }


}
