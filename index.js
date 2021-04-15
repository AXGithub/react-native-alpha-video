// main index.js

import React from 'react'
import { 
    NativeModules,
    requireNativeComponent,
    findNodeHandle,
    Platform
 } from 'react-native';

const NativeAlphaVideo = requireNativeComponent('RNAlphaVideo', AlphaVideoView)

export const AlphaVideoModule = NativeModules.RNAlphaVideoManager
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
    static play() {
        if (Platform.OS === 'ios') {
            AlphaVideoModule.play(this._redHandle)
        } else {

        }
    }

    // 暂停
    static pause() {
        if (Platform.OS === 'ios') {
            AlphaVideoModule.pause(this._redHandle)
        } else {

        }
    }

    // 释放
    static clear() {
        if (Platform.OS === 'ios') {
            AlphaVideoModule.clear(this._redHandle)
        } else {

        }
    }

    render() {
        if (!this.props.source) {
            return null
        }
        let eventListeners = {};
        eventListeners.onDidPlayFinish = (event) => {
            this.props.onDidPlayFinish();
        }
        return (
            <NativeAlphaVideo
                style={{width: }}
                {...this.props}
                {...eventListeners}
                ref={this._setReference}
            />
        )
    }
}
