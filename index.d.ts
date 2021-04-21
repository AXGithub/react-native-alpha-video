declare module 'react-native-alpha-video' {
    import React from 'react'
    import { ViewProps } from 'react-native'

    export interface IAlphaViewProps extends ViewProps {
        // 视频播放完成后，回调
        onDidPlayFinish?: () => void
        // URL，或是本地 NSBundle.mainBundle / assets 文件
        source?: string
        // 是否循环播放,默认不循环
        loop?: boolean
        // 是否静音,默认不静音
        muted?: boolean
    }
    export class AlphaVideoView extends React.Component<IAlphaViewProps, any> {
        constructor(props: Readonly<IAlphaViewProps>)
        play:() => void
        pause:() => void
        stop:() => void
        clear:() => void
    }

    export class AlphaVideoModule {
        // 预加载
        static advanceDownload(urls: Array<String>): void
        // 获取本地地址
        static getAssets(url: NodeRequire): string
    }
}
