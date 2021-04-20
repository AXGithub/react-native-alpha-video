// main index.js

import React from 'react'
import { 
    NativeModules,
    requireNativeComponent,
    findNodeHandle,
    Platform,
    Dimensions
 } from 'react-native';

const width = Dimensions.get('window').width
const height = Dimensions.get('window').height

const NativeAlphaVideo = requireNativeComponent('RNAlphaVideo', AlphaVideoView)

const _module = NativeModules.RNAlphaVideoManager || NativeModules.AlphaVideoModule
export class AlphaVideoModule {
    // 预加载
    static advanceDownload(urls) {
        _module.advanceDownload(urls)
    }
}
export class AlphaVideoView extends React.Component {

    constructor(props) {
        super(props)
    }

    /** 查找对应组件实例的tag值 */
    _setReference = (ref: ?Object) => {
        if (ref) {
            this._redHandle = findNodeHandle(ref);
        } else {
            this._redHandle = null;
        }
    }

    // 播放
    play() {
        if (Platform.OS === 'ios') {
            _module.play(this._redHandle)
        } else {
            _module.play()
        }
    }

    // 暂停
    pause() {
        if (Platform.OS === 'ios') {
            _module.pause(this._redHandle)
        } else {
            _module.pause()
        }
    }

    // 释放
    clear() {
        if (Platform.OS === 'ios') {
            _module.clear(this._redHandle)
        } else {
            _module.clear()
        }
    }

    // 停止
    stop() {
        if (Platform.OS === 'ios') {
            _module.pause(this._redHandle)
        } else {
            _module.stop()
        }
    }

    componentWillUnmount() {
        this.clear()
    }

    render() {
        if (!this.props.source) {
            return null
        }
        return (
            <NativeAlphaVideo
                style={{width: width, height: height}}
                {...this.props}
                onDidPlayFinish={() => {
                    this.props.onDidPlayFinish()
                }}
                ref={this._setReference}
            />
        )
    }
}
