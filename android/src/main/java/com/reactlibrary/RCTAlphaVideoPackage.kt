// RCTAlphaVideoPackage.java
package com.reactlibrary

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import java.util.*

class RCTAlphaVideoPackage : ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return Arrays.asList<NativeModule>(RCTAlphaVideoModule(reactContext))
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return Arrays.asList<ViewManager<*, *>>(
                RCTAlphaVideoManager())
    }
}