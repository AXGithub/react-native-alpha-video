// RCTAlphaVideoManager.java
package com.reactlibrary;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.Map;

public class RCTAlphaVideoManager extends SimpleViewManager<AlphaVideoView> {

    private static final String TAG = "RCTAlphaVideoManager";
    private AlphaVideoView videoView;

    // 事件名,这里写个enum方便循环
    public enum Events {
        //        EVENT_CODE_TYPES("codeTypes"),
        ON_DID_PLAY_FINISH("onDidPlayFinish");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String REACT_CLASS = "RNAlphaVideo";

    /**
     * 设置别名
     *
     * @return
     */
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /**
     * 初始化入口
     *
     * @param context
     * @return
     */
    @NonNull
    @Override
    protected AlphaVideoView createViewInstance(@NonNull ThemedReactContext context) {
        Activity activity = context.getCurrentActivity();
        videoView = new AlphaVideoView(activity, context);
        Log.d(TAG, "createViewInstance: 初始化入口");
        return videoView;
    }

    /**
     * 注册事件
     *
     * @return
     */
    @Override
    @Nullable
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

}
