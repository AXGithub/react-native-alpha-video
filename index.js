'use strict'

import React, { Component } from 'react'
import {
    requireNativeComponent,
    NativeModules,
    Platform
} from 'react-native';

const NativeAlphaVideoView = requireNativeComponent('RNAlphaVideo', AlphaVideoView)

export class AlphaVideoView extends Component {

    constructor(props) {
        super(props)
        this.state = {}
    }

    render() {

        return (
            <NativeAlphaVideoView />
        )
    }
}
