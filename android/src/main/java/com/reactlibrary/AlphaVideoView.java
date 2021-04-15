package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.reactlibrary.mxVideo.MxVideoView;

public class AlphaVideoView extends FrameLayout {
    private static final String TAG = "AlphaVideoView";
    private final Activity activity;
    private final ReactContext context;

    public MxVideoView videoView;

    public AlphaVideoView(Activity activity, @NonNull ReactContext context) {
        super(context);
        this.activity = activity;
        this.context = context;

        Log.d(TAG, "AlphaVideoView: init -- ");
        videoView = new MxVideoView(context, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        activity.addContentView(videoView, layoutParams);

        videoView.setVideoFromAssets("aa.mp4");
        Log.d(TAG, "AlphaVideoView: 播放了aa.mp4");
    }
}
